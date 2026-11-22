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

**Atenção:** a pasta de trabalho (Working directory) deve ser a raiz do projeto, pois o programa guarda os dados em `dados/vestuario.dat`.

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

Quando já existe `dados/vestuario.dat` de uma versão anterior, o programa **mantém os utilizadores, guarda-roupas e combinações** e acrescenta automaticamente os valores iniciais que eventualmente faltem, sem duplicar os que já existam. Não apagues o ficheiro `.dat` se quiseres preservar os dados.

Ao inserir listas nos menus de administração, escreve uma designação por linha e usa **ENTER vazio** para terminar. Nas seleções múltiplas, escreve números separados por vírgulas, por exemplo `1,2`.

## 3. Funcionalidades implementadas

- UC1 — Adicionar peça de roupa (inclui uma ou mais cores, uma ou mais estações, uma ou mais ocasiões, nome único por guarda-roupa).
- UC2 — Remover peça com confirmação.
- UC3 — Editar peça com confirmação.
- UC4 — Marcar peça como indisponível.
- UC5 — Consultar guarda-roupa.
- UC6 — Solicitar outfit para uma ocasião e estação escolhidas, segundo UC15. **Todas as peças do mesmo outfit têm de ser adequadas simultaneamente à mesma ocasião e à mesma estação do pedido**; nunca são misturadas peças de ocasiões ou estações incompatíveis. Uma peça pode estar associada a várias ocasiões e estações, desde que inclua as selecionadas. Não guarda pedidos falhados.
- UC7 — Definir listagem de cores (administrador).
- UC8 — Definir ocasiões (administrador).
- UC9 — Definir estações do ano (administrador).
- UC11 — Registar utilizador (visitante).
- UC12 — Definir tipos de roupa (administrador).
- UC13 — Avaliar outfit de 1 a 5; nova avaliação atualiza a anterior.
- UC14 — Consultar histórico de recomendações e avaliações do próprio utilizador.
- UC15 — Definir combinações possíveis de pelo menos dois tipos de roupa diferentes (administrador).

**UC10 não existe**, conforme a revisão do relatório. As peças **não têm tamanho, padrão ou características adicionais**. A interface possui textos básicos em português, inglês e francês; não há um caso de uso autónomo de «mudar idioma», apenas seleção da língua ao iniciar a aplicação.

## 4. Onde está o código

- `src/main/java/org/Main.java` — início do programa.
- `src/main/java/org/modelo` — classes do domínio (Utilizador, PecaVestuario, GuardaRoupa, TipoRoupa, Cor, Estacao, Ocasiao, CombinacaoPossivel, PedidoOutfit, Outfit, Avaliacao, etc.).
- `src/main/java/org/controladores` — regras dos casos de uso (autenticação, catálogos, guarda-roupa, outfits e gerador).
- `src/main/java/org/interfaceutilizador` — menus de consola e traduções.
- `src/main/java/org/persistencia/FicheiroDados.java` — gravação/leitura do ficheiro binário.
- `src/main/resources/mensagens_*.properties` — textos nas três línguas.
- `src/test/java/org/TestesSimples.java` — 50 verificações automáticas, sem JUnit.
- `GUIA_DO_CODIGO.md` — explicação detalhada, com exemplos.

## 5. Gravação dos dados (Serializable)

O programa **não usa uma base de dados**. Usa `ObjectOutputStream` para guardar num **ficheiro binário** (`dados/vestuario.dat`) o objeto `DadosAplicacao`, que contém utilizadores, catálogos, guarda-roupas, combinações, pedidos, outfits e avaliações.

O ficheiro é carregado na execução seguinte com `ObjectInputStream`. As classes que podem ser guardadas implementam `Serializable`. O código atual salva o conjunto dos dados após cada operação que os altera.

**Segurança:** as palavras-passe são guardadas como *hash SHA-256 com sal* e não como texto original. Este exemplo serve para fins didáticos; o ficheiro `.dat` **não está encriptado** e não deve conter dados pessoais reais. Para um sistema real seriam necessários encriptação/controlo de acesso ao armazenamento e uma função de derivação de palavras-passe apropriada, como PBKDF2 ou Argon2. Não publicar o ficheiro `.dat` no GitHub.

## 6. Testes

No IntelliJ, abrir `src/test/java/org/TestesSimples.java` e executar o método `main()`. O resultado esperado é:

`OK: 50 verificações efetuadas com sucesso.`

Os testes incluem nome de peça único, contas separadas, só administrador altera catálogos, outfit só com combinação válida, **não combinar peças de ocasiões ou estações diferentes**, exclusão de peças indisponíveis, atualização de avaliações e gravação/leitura de ficheiros.

## 7. Limitações assumidas

- A lógica do gerador escolhe **a primeira regra de combinação** que consegue satisfazer e **a primeira peça adequada** de cada tipo; não usa inteligência artificial nem otimização.
- A escala de avaliação **1 a 5** foi escolhida na implementação, pois o relatório deixa-a em aberto.
- O sistema guarda somente pedidos de outfit bem-sucedidos, conforme o UC6.
- O relatório inclui atributos conceituais como `favorito`, `ativa` e `dataRegisto`; estes existem no modelo, mas **não há um menu para os alterar**, porque não foram especificados casos de uso para isso.
- As classes Controller do design foram **agrupadas em quatro controladores simples**, preservando as responsabilidades. Esta organização reduz repetição e é adequada ao nível introdutório do projeto.
- As mensagens principais da interface estão traduzidas. Algumas mensagens técnicas de validação continuam em português para manter o código simples.
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
