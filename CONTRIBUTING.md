# Como Contribuir para o Kore

Bem-vindo ao projeto Kore! Agradecemos seu interesse em contribuir. Este documento descreve as diretrizes para contribuir com o código, documentação e outros recursos para o projeto Kore.

## Sumário

1.  [Código de Conduta](#código-de-conduta)
2.  [Como Contribuir](#como-contribuir)
    *   [Reportando Bugs](#reportando-bugs)
    *   [Sugerindo Novas Funcionalidades](#sugerindo-novas-funcionalidades)
    *   [Enviando Pull Requests](#enviando-pull-requests)
3.  [Configuração do Ambiente de Desenvolvimento](#configuração-do-ambiente-de-desenvolvimento)
4.  [Estilo de Código](#estilo-de-código)
5.  [Licença](#licença)

## Código de Conduta

Esperamos que todos os colaboradores sigam nosso Código de Conduta. Ele promove um ambiente aberto e acolhedor. Por favor, leia o [Código de Conduta](CODE_OF_CONDUCT.md) antes de contribuir.

## Como Contribuir

### Reportando Bugs

Se você encontrar um bug, por favor, abra uma issue no GitHub. Ao reportar um bug, inclua o máximo de detalhes possível:

*   Uma descrição clara e concisa do bug.
*   Passos para reproduzir o comportamento.
*   O comportamento esperado.
*   O comportamento real.
*   Versão do Kore e do Minecraft.
*   Quaisquer mensagens de erro ou logs relevantes.

### Sugerindo Novas Funcionalidades

Se você tiver uma ideia para uma nova funcionalidade, por favor, abra uma issue no GitHub para discuti-la. Isso nos permite discutir a ideia, sua viabilidade e como ela se encaixa na visão geral do projeto.

### Enviando Pull Requests

1.  **Faça um Fork do Repositório**: Comece fazendo um fork do repositório Kore para sua conta GitHub.
2.  **Clone o Repositório**: Clone seu fork para sua máquina local:
    ```bash
    git clone https://github.com/SEU_USUARIO/Kore.git
    cd Kore
    ```
3.  **Crie uma Nova Branch**: Crie uma nova branch para suas alterações. Use um nome descritivo para a branch (ex: `feature/nova-funcionalidade` ou `bugfix/correcao-de-erro`).
    ```bash
    git checkout -b feature/sua-nova-funcionalidade
    ```
4.  **Faça Suas Alterações**: Implemente suas alterações, garantindo que elas sigam o [Estilo de Código](#estilo-de-código) do projeto.
5.  **Teste Suas Alterações**: Certifique-se de que suas alterações não introduzam novos bugs e que as funcionalidades existentes continuem funcionando corretamente. Se possível, adicione testes para suas novas funcionalidades ou correções.
6.  **Commit Suas Alterações**: Escreva mensagens de commit claras e concisas. Cada commit deve representar uma única alteração lógica.
    ```bash
    git commit -m "feat: Adiciona nova funcionalidade X"
    ```
7.  **Envie para o Seu Fork**: Envie suas alterações para o seu fork no GitHub.
    ```bash
    git push origin feature/sua-nova-funcionalidade
    ```
8.  **Abra um Pull Request**: Vá para o repositório original do Kore no GitHub e abra um novo Pull Request da sua branch para a branch `main` (ou a branch de desenvolvimento apropriada). Forneça uma descrição detalhada de suas alterações e referencie quaisquer issues relevantes.

## Configuração do Ambiente de Desenvolvimento

Para configurar seu ambiente de desenvolvimento, siga as instruções de instalação no [README.md](README.md).

## Estilo de Código

O projeto Kore segue as diretrizes de estilo de código Kotlin padrão. Por favor, use o formatador de código da sua IDE para garantir a consistência.

## Licença

Ao contribuir para o Kore, você concorda que suas contribuições serão licenciadas sob a licença [MIT](LICENSE) do projeto.


