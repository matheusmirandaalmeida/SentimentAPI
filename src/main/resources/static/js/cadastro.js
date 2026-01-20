document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formCadastro');
    const emailInput = document.getElementById('emailCadastro');
    const senhaInput = document.getElementById('senhaCadastro');
    const confirmarInput = document.getElementById('confirmarSenha');
    const tipoUsuarioRadios = document.querySelectorAll('input[name="tipoUsuario"]');
    const camposEmpresa = document.getElementById('camposEmpresa');
    const nomeEmpresaInput = document.getElementById('nomeEmpresa');
    const cnpjInput = document.getElementById('cnpj');
    const termosCheckbox = document.getElementById('termos');

    // Elementos de erro
    const emailError = document.getElementById('emailError');
    const senhaError = document.getElementById('senhaError');
    const confirmarError = document.getElementById('confirmarError');
    const empresaError = document.getElementById('empresaError');
    const cnpjError = document.getElementById('cnpjError');

    // URL da API Spring
    const API_BASE_URL = window.AppConfig ? window.AppConfig.getApiBaseUrl() : '';

    // Mostrar/ocultar campos da empresa
    tipoUsuarioRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            if (this.value === 'EMPRESA') {
                camposEmpresa.style.display = 'block';
            } else {
                camposEmpresa.style.display = 'none';
            }
        });
    });

    // Validação em tempo real
    emailInput.addEventListener('input', validateEmail);
    senhaInput.addEventListener('input', validatePassword);
    confirmarInput.addEventListener('input', validatePasswordConfirmation);

    function validateEmail() {
        const email = emailInput.value.trim();
        const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

        if (email === '') {
            emailError.textContent = 'E-mail é obrigatório';
            emailError.classList.add('show');
            emailInput.classList.add('error');
            return false;
        } else if (!isValid) {
            emailError.textContent = 'Por favor, insira um e-mail válido';
            emailError.classList.add('show');
            emailInput.classList.add('error');
            return false;
        } else {
            emailError.classList.remove('show');
            emailInput.classList.remove('error');
            return true;
        }
    }

    function validatePassword() {
        const senha = senhaInput.value;

        if (senha.length < 6) {
            senhaError.textContent = 'A senha deve ter pelo menos 6 caracteres';
            senhaError.classList.add('show');
            senhaInput.classList.add('error');
            return false;
        } else {
            senhaError.classList.remove('show');
            senhaInput.classList.remove('error');

            // Valida confirmação também
            validatePasswordConfirmation();
            return true;
        }
    }

    function validatePasswordConfirmation() {
        const senha = senhaInput.value;
        const confirmar = confirmarInput.value;

        if (confirmar === '') {
            confirmarError.textContent = 'Por favor, confirme sua senha';
            confirmarError.classList.add('show');
            confirmarInput.classList.add('error');
            return false;
        } else if (senha !== confirmar) {
            confirmarError.textContent = 'As senhas não coincidem';
            confirmarError.classList.add('show');
            confirmarInput.classList.add('error');
            return false;
        } else {
            confirmarError.classList.remove('show');
            confirmarInput.classList.remove('error');
            return true;
        }
    }

    function validateEmpresa() {
        const tipoUsuario = document.querySelector('input[name="tipoUsuario"]:checked').value;
        const nomeEmpresa = nomeEmpresaInput.value.trim();
        const cnpj = cnpjInput.value.trim();

        if (tipoUsuario === 'EMPRESA') {
            let isValid = true;

            // Validar nome da empresa
            if (nomeEmpresa === '') {
                empresaError.textContent = 'Nome da empresa é obrigatório';
                empresaError.classList.add('show');
                nomeEmpresaInput.classList.add('error');
                isValid = false;
            } else {
                empresaError.classList.remove('show');
                nomeEmpresaInput.classList.remove('error');
            }

            // Validar CNPJ
            if (cnpj === '') {
                cnpjError.textContent = 'CNPJ é obrigatório';
                cnpjError.classList.add('show');
                cnpjInput.classList.add('error');
                isValid = false;
            } else if (!validarCNPJ(cnpj)) {
                cnpjError.textContent = 'CNPJ inválido';
                cnpjError.classList.add('show');
                cnpjInput.classList.add('error');
                isValid = false;
            } else {
                cnpjError.classList.remove('show');
                cnpjInput.classList.remove('error');
            }

            return isValid;
        }

        return true;
    }

    // Função para validar CNPJ
    function validarCNPJ(cnpj) {
        cnpj = cnpj.replace(/[^\d]+/g, '');

        if (cnpj.length !== 14) return false;

        // Elimina CNPJs inválidos conhecidos
        if (/^(\d)\1+$/.test(cnpj)) return false;

        // Valida DVs
        let tamanho = cnpj.length - 2;
        let numeros = cnpj.substring(0, tamanho);
        let digitos = cnpj.substring(tamanho);
        let soma = 0;
        let pos = tamanho - 7;

        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }

        let resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        if (resultado != digitos.charAt(0)) return false;

        tamanho = tamanho + 1;
        numeros = cnpj.substring(0, tamanho);
        soma = 0;
        pos = tamanho - 7;

        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }

        resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        if (resultado != digitos.charAt(1)) return false;

        return true;
    }

    function showMessage(text, type = 'error') {
        const messageDiv = document.getElementById('message');
        messageDiv.textContent = text;
        messageDiv.className = `message ${type}`;
        messageDiv.style.display = 'block';

        // Auto-esconde mensagens de sucesso
        if (type === 'success') {
            setTimeout(() => {
                messageDiv.style.display = 'none';
            }, 3000);
        }
    }

    function setLoading(isLoading) {
        const submitBtn = document.getElementById('submitBtn');
        const btnText = document.getElementById('btnText');
        const btnSpinner = document.getElementById('btnSpinner');

        if (isLoading) {
            submitBtn.disabled = true;
            btnText.style.display = 'none';
            btnSpinner.style.display = 'block';
        } else {
            submitBtn.disabled = false;
            btnText.style.display = 'block';
            btnSpinner.style.display = 'none';
        }
    }

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        // Validar termos
        if (!termosCheckbox.checked) {
            showMessage('Você precisa aceitar os termos de uso', 'error');
            return;
        }

        // Validar todos os campos
        const isEmailValid = validateEmail();
        const isPasswordValid = validatePassword();
        const isConfirmValid = validatePasswordConfirmation();
        const isEmpresaValid = validateEmpresa();

        if (!isEmailValid || !isPasswordValid || !isConfirmValid || !isEmpresaValid) {
            showMessage('Por favor, corrija os erros no formulário.', 'error');
            return;
        }

        // Preparar dados para envio
        const tipoUsuario = document.querySelector('input[name="tipoUsuario"]:checked').value;
        const userData = {
            email: emailInput.value.trim(),
            senha: senhaInput.value,
            tipoUsuario: tipoUsuario
        };

        // Adicionar campos da empresa se for o caso
        if (tipoUsuario === 'EMPRESA') {
            userData.nomeEmpresa = nomeEmpresaInput.value.trim();
            userData.cnpj = cnpjInput.value.replace(/\D/g, ''); // Remove formatação
        }

        setLoading(true);

        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData)
            });

            if (response.ok) {
                showMessage('Cadastro realizado com sucesso! Redirecionando para login...', 'success');

                // Aguarda 2 segundos antes de redirecionar
                setTimeout(() => {
                    window.location.href = '/login.html';
                }, 2000);
            } else {
                let errorMessage = 'Falha no cadastro';

                try {
                    const errorData = await response.json();
                    if (errorData.erro) {
                        errorMessage = errorData.erro;
                    } else if (errorData.mensagem) {
                        errorMessage = errorData.mensagem;
                    }
                } catch {
                    // Se não conseguir parsear JSON
                    const text = await response.text();
                    if (text) errorMessage = text;
                }

                showMessage(errorMessage, 'error');
            }
        } catch (error) {
            console.error('Erro na requisição:', error);
            showMessage('Erro de conexão. Verifique sua internet e tente novamente.', 'error');
        } finally {
            setLoading(false);
        }
    });

    // mascara ao CNPJ
    cnpjInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');

        if (value.length <= 14) {
            value = value.replace(/^(\d{2})(\d)/, '$1.$2');
            value = value.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3');
            value = value.replace(/\.(\d{3})(\d)/, '.$1/$2');
            value = value.replace(/(\d{4})(\d)/, '$1-$2');
        }

        e.target.value = value;
    });
});