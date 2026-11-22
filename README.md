# Assistente Digital de Vestuário — Iteração 2

Aplicação de **consola em Java 17**, baseada no relatório `relatorioIT2Grupo01_sem_caracteristicas`. É um projeto académico de Programação Orientada por Objetos, sem bibliotecas externas, bases de dados SQL, Spring ou interfaces gráficas.

## 1. Abrir no IntelliJ IDEA

1. Descompactar o ZIP numa pasta normal do computador.
2. Abrir o **IntelliJ IDEA** e escolher **File > Open**.
3. Selecionar a pasta `AssistenteDigitalVestuario_IT2` (onde está `pom.xml`).
4. Se for pedido, aceitar **Load Maven Project**. Não é necessário descarregar dependências externas.
5. Em **File > Project Structure > Project SDK**, escolher **JDK 17 ou superior**.
6. Abrir `src/main/java/org/Main.java` e clicar no triângulo verde ao lado de `main()`; escolher **Run 'Main.main()'**.
7. Na consola, escolher o idioma da interface: `1` português, `2` inglês, `3` francês.

**Atenção:** a pasta de trabalho (Working directory) deve ser a raiz do projeto, pois é aí que o programa procura a pasta `dados`.

## 2. Primeira utilização e catálogos predefinidos

A aplicação **já traz 7 cores, 5 ocasiões, 4 estações e 5 tipos de roupa**. O utilizador pode registar peças imediatamente, sem esperar que o administrador crie esses catálogos.

| Catálogo | Valores disponíveis logo no início |
|---|---|
| 7 cores | Preto, Branco, Azul, Vermelho, Verde, Amarelo, Cinzento |
| 5 ocasiões | Trabalho, Aulas, Jantar, Casamento, Dia a dia |
| 4 estações | Primavera, Verão, Outono, Inverno |
| 5 tipos de roupa | Calças, Camisola, Casaco, T-shirt, Vestido |

Na primeira execução, o programa pede a criação de uma conta de **administrador**. Não existe palavra-passe predefinida: guarda o email e a palavra-passe escolhidos.

Depois, podes:

1. Entrar como administrador e **acrescentar** cores, ocasiões, estações ou tipos de roupa, através dos menus UC7, UC8, UC9 e UC12. Os valores já existentes não podem ser registados outra vez.
2. Definir pelo menos uma **combinação possível** (UC15), como `Calças + Camisola`, para que o sistema consiga gerar sugestões de outfit.
3. Terminar sessão, registar um utilizador (UC11) e entrar na respetiva conta.
4. Adicionar peças (UC1), escolhendo os valores dos catálogos já preenchidos, incluindo a estação e ocasião de cada peça.
5. Solicitar um outfit (UC6), avaliá-lo (UC13) e consultar o histórico (UC14).

**Nota:** adicionar uma peça não depende da existência de combinações. Para **gerar um outfit**, contudo, tem de existir uma regra de combinação definida pelo administrador e peças disponíveis que satisfaçam a mesma ocasião e estação escolhidas no pedido.

Na primeira abertura de um `dados/vestuario.dat` antigo, o programa migra as contas e os restantes dados para um novo ficheiro cifrado. O original é preservado. Os valores iniciais em falta são acrescentados sem duplicações.

Ao inserir listas nos menus de administração, escreve uma designação por linha e usa **ENTER vazio** para terminar. Nas seleções múltiplas, escreve números separados por vírgulas, por exemplo `1,2`.

## 3. Funcionalidades implementadas

- UC1 — Adicionar peça de roupa (inclui uma ou mais cores, uma ou mais estações, uma ou mais ocasiões, nome único por guarda-roupa).
- UC2 — Remover peça com confirmação.
- UC3 — Editar peça com confirmação.
- UC4 — Marcar peça como indisponível.
- UC5 — Consultar guarda-roupa.
- UC6 — Solicitar outfit para uma ocasião e estação escolhidas, segundo UC15 e UC16. **Todas as peças do mesmo outfit têm de ser adequadas simultaneamente à mesma ocasião e à mesma estação do pedido**; nunca são misturadas peças de ocasiões ou estações incompatíveis. Uma peça pode estar associada a várias ocasiões e estações, desde que inclua as selecionadas. Não guarda pedidos falhados.
- UC7 — Definir listagem de cores (administrador).
- UC8 — Definir ocasiões (administrador).
- UC9 — Definir estações do ano (administrador).
- UC11 — Registar utilizador (visitante).
- UC12 — Definir tipos de roupa (administrador).
- UC13 — Avaliar outfit de 1 a 5; nova avaliação atualiza a anterior.
- UC14 — Consultar histórico de recomendações e avaliações do próprio utilizador.
- UC15 — Definir combinações possíveis de pelo menos dois tipos de roupa diferentes (administrador).
- UC16 — Definir combinações possíveis de pelo menos duas cores diferentes (administrador).

**UC10 não existe**, conforme a revisão do relatório. As peças **não têm tamanho, padrão ou características adicionais**. A interface está disponível em português, inglês e francês. Podes mudar o idioma no menu principal durante a execução.

## 4. Onde está o código

- `src/main/java/org/Main.java` — início do programa.
- `src/main/java/org/modelo` — classes do domínio (Administrador, Utilizador, PecaVestuario, GuardaRoupa, TipoRoupa, Cor, Estacao, Ocasiao, CombinacaoPossivel, PedidoOutfit, Outfit, Avaliacao, etc.).
- `src/main/java/org/controladores` — regras dos casos de uso (autenticação, catálogos, guarda-roupa, outfits e gerador).
- `src/main/java/org/interfaceutilizador` — menus de consola e traduções.
- `src/main/java/org/persistencia/FicheiroDados.java` — gravação/leitura do ficheiro binário.
- `src/main/resources/mensagens_*.properties` — textos nas três línguas.
- `src/test/java/org/TestesSimples.java` — verificações automáticas, sem JUnit.
- `GUIA_DO_CODIGO.md` — explicação detalhada, com exemplos.

## 5. Gravação dos dados (Serializable)

A aplicação serializa `DadosAplicacao` em memória, cifra os bytes com AES-GCM e grava-os em `dados/vestuario.dat.enc`. Ao abrir, usa `dados/vestuario.dat.key` para decifrar e depois reconstrói os objetos com `ObjectInputStream`. As alterações são gravadas após as operações que modificam os dados.

Para recuperar os dados noutro computador, guarda uma cópia do **ficheiro cifrado e da chave correspondente**. Mantém a chave privada. O antigo `dados/vestuario.dat`, quando existe, continua a ser uma cópia legada sem cifragem. Mantém-no protegido e fora do GitHub, tal como a chave.

As palavras-passe são guardadas como hash SHA-256 com sal. A proteção do ficheiro cifrado depende do acesso à chave.

## 6. Testes

No IntelliJ, abrir `src/test/java/org/TestesSimples.java` e executar o método `main()`. A execução imprime o total de verificações que passaram.

Os testes incluem nome de peça único, contas separadas, só administrador altera catálogos, outfit só com combinação válida, **não combinar peças de ocasiões ou estações diferentes**, exclusão de peças indisponíveis, atualização de avaliações e gravação/leitura de ficheiros.

## 7. Limitações assumidas

- O gerador prefere outfits com até três cores distintas e sorteia entre peças elegíveis equivalentes do mesmo tipo e com as mesmas cores.
- A escala de avaliação **1 a 5** foi escolhida na implementação, pois o relatório deixa-a em aberto.
- O sistema guarda somente pedidos de outfit bem-sucedidos, conforme o UC6.
- O relatório inclui atributos conceituais como `favorito`, `ativa` e `dataRegisto`; estes existem no modelo, mas **não há um menu para os alterar**, porque não foram especificados casos de uso para isso.
- As classes Controller do design foram **agrupadas em quatro controladores simples**, preservando as responsabilidades. Esta organização reduz repetição e é adequada ao nível introdutório do projeto.
- Não há garantia demonstrada de resposta inferior a 3 segundos em 96% dos pedidos nem limites verificados de CPU/RAM. Esses são requisitos **a validar com testes de desempenho**, não resultados da implementação.
- Os dados ficam guardados num ficheiro local, não em rede nem em SQL; não existe autenticação robusta para ambiente de produção.
- Se uma peça antiga for editada, o histórico mantém a referência à peça e pode mostrar o **nome atualizado**. O relatório não fixa uma política de versões históricas.

## 8. Compilar sem Maven (opcional)

Em Linux/macOS, no diretório do projeto:

```bash
mkdir -p out
find src/main/java -name '*.java' > sources.txt
javac -encoding UTF-8 -d out @sources.txt
cp src/main/resources/*.properties out/
java -cp out org.Main
```

No Windows, o caminho mais fácil é executar diretamente pelo IntelliJ.
