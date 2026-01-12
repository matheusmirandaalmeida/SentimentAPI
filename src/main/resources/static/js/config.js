// Configurações dinâmicas da aplicação
window.AppConfig = {
    // Detecta ambiente
    getApiBaseUrl: function() {
        const hostname = window.location.hostname;
        const port = window.location.port;

        // Se estiver no Live Server (5500), aponta para Spring (8080)
        if (hostname === 'localhost' && port === '5500') {
            return 'http://localhost:8080';
        }

        // Se estiver no Spring Boot (8080), usa relativo (mesma origem)
        if (hostname === 'localhost' && port === '8080') {
            return ''; // URLs relativas
        }

        // Para produção (sem porta ou porta diferente)
        return ''; // URLs relativas
    },

    // Método helper para construir URLs da API
    apiUrl: function(endpoint) {
        const base = this.getApiBaseUrl();
        // Remove barras duplicadas
        return base + (endpoint.startsWith('/') ? endpoint : '/' + endpoint);
    },

    // Configurações de CORS
    corsConfig: {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        }
    }
};