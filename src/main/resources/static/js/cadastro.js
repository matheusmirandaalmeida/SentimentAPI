document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formCadastro');
    const emailInput = document.getElementById('emailCadastro');
    const senhaInput = document.getElementById('senhaCadastro');
    const confirmarInput = document.getElementById('confirmarSenha');
    const tipoUsuarioRadios = document.querySelectorAll('input[name="tipoUsuario"]');
    const camposEmpresa = document.getElementById('camposEmpresa');
    const nomeEmpresaInput = document.getElementById('nomeEmpresa');
    const cnpjInput = document.getElementById('cnpj');
    const submitBtn = document.getElementById('submitBtn');
    const btnText = document.getElementById('btnText');
    const btnSpinner = document.getElementById('btnSpinner');
    const messageDiv = document.getElementById('message');

    // Elementos de erro
    const emailError = document.getElementById('emailError');
    const senhaError = document.getElementById('senhaError');
    const confirmarError = document.getElementById('confirmarError');
    const empresaError = document.getElementById('empresaError');
    const cnpjError = document.getElementById('cnpjError');

    const API_BASE_URL = window.AppConfig?.getApiBaseUrl() || '';

    // Mostrar/ocultar campos de empresa
    tipoUsuarioRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            const isEmpresa = this.value === 'EMPRESA';

            if (isEmpresa) {
                camposEmpresa.style.display = 'block';
                // Pequeno delay para transição CSS
                setTimeout(() => {
                    camposEmpresa.classList.add('show');
                }, 10);

                // Tornar campos obrigatórios
                nomeEmpresaInput.required = true;
                cnpjInput.required = true;
            } else {
                camposEmpresa.classList.remove('show');
                setTimeout(() => {
                    camposEmpresa.style.display = 'none';
                }, 300);

                // Remover obrigatoriedade
                nomeEmpresaInput.required = false;
                cnpjInput.required = false;

                // Limpar valores
                nomeEmpresaInput.value = '';
                cnpjInput.value = '';

                // Limpar erros visuais
                empresaError.classList.remove('show');
                cnpjError.classList.remove('show');
                nomeEmpresaInput.classList.remove('error');
                cnpjInput.classList.remove('error');
            }
        });
    });

    // Validação em tempo real
    emailInput.addEventListener('input', validateEmail);
    senhaInput.addEventListener('input', validatePassword);
    confirmarInput.addEventListener('input', validatePasswordConfirmation);

    nomeEmpresaInput.addEventListener('input', validateEmpresa);

    cnpjInput.addEventListener('input', function(e) {
        // Aplicar máscara
        let value = e.target.value.replace(/\D/g, '');
        if (value.length <= 14) {
            value = value.replace(/^(\d{2})(\d)/, '$1.$2');
            value = value.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3');
            value = value.replace(/\.(\d{3})(\d)/, '.$1/$2');
            value = value.replace(/(\d{4})(\d)/, '$1-$2');
        }
        e.target.value = value;

        // Validar
        validateCnpj();
    });

    function validateEmail() {
        const email = emailInput.value.trim();
        const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

        if (!email) {
            emailError.textContent = 'E-mail é obrigatório';
            showError(emailError, emailInput);
            return false;
        } else if (!isValid) {
            emailError.textContent = 'Por favor, insira um e-mail válido';
            showError(emailError, emailInput);
            return false;
        } else {
            hideError(emailError, emailInput);
            return true;
        }
    }

    function validatePassword() {
        const senha = senhaInput.value;

        if (senha.length < 6) {
            senhaError.textContent = 'A senha deve ter pelo menos 6 caracteres';
            showError(senhaError, senhaInput);
            return false;
        } else {
            hideError(senhaError, senhaInput);
            validatePasswordConfirmation();
            return true;
        }
    }

    function validatePasswordConfirmation() {
        const senha = senhaInput.value;
        const confirmar = confirmarInput.value;

        if (!confirmar) {
            confirmarError.textContent = 'Por favor, confirme sua senha';
            showError(confirmarError, confirmarInput);
            return false;
        } else if (senha !== confirmar) {
            confirmarError.textContent = 'As senhas não coincidem';
            showError(confirmarError, confirmarInput);
            return false;
        } else {
            hideError(confirmarError, confirmarInput);
            return true;
        }
    }

    function validateEmpresa() {
        const nomeEmpresa = nomeEmpresaInput.value.trim();
        const isEmpresa = document.querySelector('input[name="tipoUsuario"]:checked')?.value === 'EMPRESA';

        if (isEmpresa && !nomeEmpresa) {
            empresaError.textContent = 'Nome da empresa é obrigatório';
            showError(empresaError, nomeEmpresaInput);
            return false;
        } else {
            hideError(empresaError, nomeEmpresaInput);
            return true;
        }
    }

    function validateCnpj() {
        const cnpj = cnpjInput.value.replace(/\D/g, '');
        const isEmpresa = document.querySelector('input[name="tipoUsuario"]:checked')?.value === 'EMPRESA';

        if (!isEmpresa) {
            hideError(cnpjError, cnpjInput);
            return true;
        }

        if (!cnpj) {
            cnpjError.textContent = 'CNPJ é obrigatório para empresas';
            showError(cnpjError, cnpjInput);
            return false;
        } else if (cnpj.length !== 14) {
            cnpjError.textContent = 'CNPJ deve ter 14 dígitos';
            showError(cnpjError, cnpjInput);
            return false;
        } else if (!validarCNPJ(cnpj)) {
            cnpjError.textContent = 'CNPJ inválido';
            showError(cnpjError, cnpjInput);
            return false;
        } else {
            hideError(cnpjError, cnpjInput);
            return true;
        }
    }

    // Função auxiliar para validar CNPJ
    function validarCNPJ(cnpj) {
        cnpj = cnpj.replace(/[^\d]+/g, '');

        if (cnpj === '' || cnpj.length !== 14) return false;

        // Elimina CNPJs inválidos conhecidos
        const invalidos = [
            "00000000000000", "11111111111111", "22222222222222",
            "33333333333333", "44444444444444", "55555555555555",
            "66666666666666", "77777777777777", "88888888888888",
            "99999999999999"
        ];
        if (invalidos.includes(cnpj)) return false;

        // Validação dos dígitos verificadores
        let tamanho = cnpj.length - 2;
        let numeros = cnpj.substring(0, tamanho);
        let digitos = cnpj.substring(tamanho);
        let soma = 0;
        let pos = tamanho - 7;

        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }

        let resultado = soma % 11 < 2 ? 0 : 11 - (soma % 11);
        if (resultado != digitos.charAt(0)) return false;

        tamanho = tamanho + 1;
        numeros = cnpj.substring(0, tamanho);
        soma = 0;
        pos = tamanho - 7;

        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }

        resultado = soma % 11 < 2 ? 0 : 11 - (soma % 11);
        return resultado == digitos.charAt(1);
    }

    function showError(errorElement, inputElement) {
        errorElement.classList.add('show');
        inputElement.classList.add('error');
    }

    function hideError(errorElement, inputElement) {
        errorElement.classList.remove('show');
        inputElement.classList.remove('error');
    }

    function showMessage(text, type = 'error') {
        messageDiv.textContent = text;
        messageDiv.className = `message ${type}`;
        messageDiv.style.display = 'block';

        if (type === 'success') {
            setTimeout(() => {
                messageDiv.style.display = 'none';
            }, 3000);
        }
    }

    function setLoading(isLoading) {
        submitBtn.disabled = isLoading;
        btnText.style.display = isLoading ? 'none' : 'block';
        btnSpinner.style.display = isLoading ? 'block' : 'none';
    }

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const isEmailValid = validateEmail();
        const isPasswordValid = validatePassword();
        const isConfirmValid = validatePasswordConfirmation();
        const isEmpresaValid = validateEmpresa();
        const isCnpjValid = validateCnpj();

        const tipoUsuario = document.querySelector('input[name="tipoUsuario"]:checked')?.value;
        const isEmpresa = tipoUsuario === 'EMPRESA';

        if (!isEmailValid || !isPasswordValid || !isConfirmValid ||
            (isEmpresa && (!isEmpresaValid || !isCnpjValid))) {
            showMessage('Por favor, corrija os erros no formulário.', 'error');
            return;
        }

        const userData = {
            email: emailInput.value.trim(),
            senha: senhaInput.value,
            tipoUsuario: tipoUsuario
        };

        if (isEmpresa) {
            userData.nomeEmpresa = nomeEmpresaInput.value.trim();
            userData.cnpj = cnpjInput.value.replace(/\D/g, '');
        }

        setLoading(true);

        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(userData)
            });

            const responseText = await response.text();

            if (response.ok) {
                showMessage('Cadastro realizado com sucesso! Redirecionando...', 'success');
                setTimeout(() => {
                    window.location.href = '/login.html';
                }, 1500);
            } else {
                let errorMessage = 'Falha no cadastro';
                try {
                    const errorData = JSON.parse(responseText);
                    errorMessage = errorData.mensagem || errorData.message || errorMessage;
                } catch {
                    if (responseText) errorMessage = responseText;
                }
                showMessage(errorMessage, 'error');
            }
        } catch (error) {
            console.error('Erro:', error);
            showMessage('Erro de conexão. Tente novamente.', 'error');
        } finally {
            setLoading(false);
        }
    });

    // Estilos CSS inline
    const style = document.createElement('style');
    style.textContent = `
        .spinner {
            display: inline-block;
            width: 20px;
            height: 20px;
            border: 3px solid rgba(255,255,255,.3);
            border-radius: 50%;
            border-top-color: #fff;
            animation: spin 1s ease-in-out infinite;
        }
        @keyframes spin {
            to { transform: rotate(360deg); }
        }
        .campos-empresa {
            transition: opacity 0.3s ease, max-height 0.3s ease;
            opacity: 0;
            max-height: 0;
            overflow: hidden;
        }
        .campos-empresa.show {
            opacity: 1;
            max-height: 300px;
        }
    `;
    document.head.appendChild(style);
});