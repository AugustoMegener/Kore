# Kore: Um Framework de Desenvolvimento para Minecraft

Kore é um framework de desenvolvimento robusto e modular projetado para simplificar e acelerar a criação de mods para Minecraft. Ele oferece uma base sólida com funcionalidades pré-construídas e uma arquitetura extensível, permitindo que desenvolvedores se concentrem na lógica de seus mods sem se preocuparem com a complexidade de baixo nível da integração com o jogo.

## Funcionalidades Principais

- **Estrutura Modular**: Facilita a organização do código e a reutilização de componentes.
- **Abstrações de Alto Nível**: Simplifica interações complexas com o jogo, como registro de itens, blocos e entidades.
- **Ferramentas de Desenvolvimento**: Inclui utilitários para depuração, testes e automação de tarefas comuns.
- **Compatibilidade**: Projetado para ser compatível com as versões mais recentes do Minecraft e suas APIs de modding.

## Por que usar Kore?

Desenvolver mods para Minecraft pode ser uma tarefa desafiadora devido à sua complexidade e à necessidade de lidar com a API do jogo em um nível detalhado. Kore visa mitigar esses desafios, fornecendo:

- **Produtividade Aumentada**: Reduza o tempo de desenvolvimento com componentes reutilizáveis e abstrações.
- **Código Mais Limpo**: Encoraja uma arquitetura de código organizada e de fácil manutenção.
- **Curva de Aprendizagem Suavizada**: Ajuda novos desenvolvedores a entrar no mundo do modding de Minecraft mais rapidamente.
- **Comunidade Ativa**: Beneficie-se de uma comunidade de desenvolvedores que utilizam e contribuem para o framework.

## Primeiros Passos

Para começar a desenvolver com Kore, siga as instruções de instalação e configuração detalhadas na seção [Instalação](#instalação).




## Instalação

Este repositório de template pode ser clonado diretamente para iniciar um novo mod. Basta criar um novo repositório clonado a partir deste, seguindo as instruções em [GitHub](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template).

Uma vez que você tenha seu clone, basta abrir o repositório na IDE de sua escolha. A recomendação usual para uma IDE é IntelliJ IDEA ou Eclipse.

> **Nota**: Para Eclipse, use as tarefas em `Launch Group` em vez das encontradas em `Java Application`. Uma tarefa de preparação deve ser executada antes de iniciar o jogo. NeoGradle usa grupos de lançamento para fazer isso subsequentemente.

Se a qualquer momento você estiver com falta de bibliotecas em sua IDE, ou tiver problemas, você pode executar `gradlew --refresh-dependencies` para atualizar o cache local. `gradlew clean` para resetar tudo (isso não afeta seu código) e então iniciar o processo novamente.

## Nomes de Mapeamento

Por padrão, o MDK é configurado para usar os nomes de mapeamento oficiais da Mojang para métodos e campos na base de código do Minecraft. Esses nomes são cobertos por uma licença específica. Todos os modders devem estar cientes desta licença. Para o texto da licença mais recente, consulte o próprio arquivo de mapeamento, ou a cópia de referência aqui: [Mojang.md](https://github.com/NeoForged/NeoForm/blob/main/Mojang.md)

## Recursos Adicionais

- Documentação da Comunidade: [https://docs.neoforged.net/](https://docs.neoforged.net/)
- Discord NeoForged: [https://discord.neoforged.net/](https://discord.neoforged.net/)




## Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.


