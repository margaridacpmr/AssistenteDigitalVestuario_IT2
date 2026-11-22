# Atualização — catálogos predefinidos

Ao iniciar, a aplicação já contém:

- 7 cores: Preto, Branco, Azul, Vermelho, Verde, Amarelo, Cinzento.
- 5 ocasiões: Trabalho, Aulas, Jantar, Casamento, Dia a dia.
- 4 estações: Primavera, Verão, Outono, Inverno.
- 5 tipos: Calças, Camisola, Casaco, T-shirt, Vestido.

## Como atualizar o projeto já aberto no IntelliJ

1. Fecha a execução do programa (botão Stop) e, de preferência, fecha o IntelliJ antes de copiar.
2. Guarda uma cópia de `dados/vestuario.dat` se já tens utilizadores e peças registados.
3. Extrai o ficheiro ZIP de atualização na pasta-raiz do projeto atual, substituindo apenas os ficheiros que vêm no ZIP (pastas `src`, README e guia).
4. Não elimines a pasta `dados` nem substituas o ficheiro `vestuario.dat`.
5. Volta a abrir o projeto e executa `src/main/java/org/Main.java`.

Também podes abrir o ZIP com o projeto completo como um projeto novo. Para recuperar os teus dados antigos, copia o ficheiro `dados/vestuario.dat` do projeto anterior para a nova pasta `dados`, antes de executar a nova versão.

Os ficheiros de dados antigos serão lidos normalmente e os valores predefinidos em falta serão acrescentados sem duplicação de designações iguais (ignorando maiúsculas e minúsculas).

A criação de peças usa diretamente estes catálogos. As regras de combinação de outfits continuam a ser definidas pelo administrador e o gerador continua a exigir que todas as peças sejam adequadas à ocasião E à estação escolhidas no pedido.

Não existe UC10 de características adicionais nem atributos de tamanho ou padrão.

## Testar

Executar `src/test/java/org/TestesSimples.java`. Resultado esperado:

    OK: 50 verificações efetuadas com sucesso.
