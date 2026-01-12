document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formCadastro');
    const emailInput = document.getElementById('emailCadastro');
    const senhaInput = document.getElementById('senhaCadastro');
    const confirmarInput = document.getElementById('confirmarSenha');
    const submitBtn = document.getElementById('submitBtn');
    const btnText = document.getElementById('btnText');
    const btnSpinner = document.getElementById('btnSpinner');
    const messageDiv = document.getElementById('message');
    
    // Elementos de erro
    const emailError = document.getElementById('emailError');
    const senhaError = document.getElementById('senhaError');
    const confirmarError = document.getElementById('confirmarError');
    
    // URL da API Spring (ajuste conforme necessário)
    const API_BASE_URL = 'http://localhost:8080';
    
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
    
    function showMessage(text, type = 'error') {
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
        
        // Valida todos os campos
        const isEmailValid = validateEmail();
        const isPasswordValid = validatePassword();
        const isConfirmValid = validatePasswordConfirmation();
        
        if (!isEmailValid || !isPasswordValid || !isConfirmValid) {
            showMessage('Por favor, corrija os erros no formulário.', 'error');
            return;
        }
        
        const userData = {
            email: emailInput.value.trim(),
            senha: senhaInput.value
        };
        
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
                showMessage('Cadastro realizado com sucesso! Redirecionando...', 'success');
                
                // Aguarda um pouco antes de redirecionar
                setTimeout(() => {
                    window.location.href = '/login.html';
                }, 1500);
            } else {
                // Tenta obter a mensagem de erro
                let errorMessage = 'Falha no cadastro';

                try {
                    const errorData = await response.json();
                    if (errorData.mensagem) {
                        errorMessage = errorData.mensagem;
                    } else if (errorData.message) {
                        errorMessage = errorData.message;
                    } else if (typeof errorData === 'string') {
                        errorMessage = errorData;
                    }
                } catch {
                    // Se não conseguir parsear JSON, usa o texto da resposta
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
    `;
    document.head.appendChild(style);
});