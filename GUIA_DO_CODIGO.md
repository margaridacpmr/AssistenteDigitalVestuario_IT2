# Guia para compreender o código Java

Este guia utiliza a terminologia do relatório de **Assistente Digital de Vestuário — Iteração 2**. Explica o projeto numa sequência que pode ser utilizada numa apresentação de segundo ano de Engenharia de Sistemas.

### Regra obrigatória: mesma ocasião e mesma estação (UC6)

Quando o utilizador pede um outfit para uma ocasião e uma estação (por exemplo, **Trabalho** e **Inverno**), **cada uma** das peças selecionadas tem de estar disponível e de ser adequada **a Trabalho e a Inverno ao mesmo tempo**. O sistema não mistura peças de ocasiões nem de estações incompatíveis só para conseguir completar uma regra de combinação.

No código, a verificação está em `GeradorOutfits.procurarPecas(...)`, usando as mesmas variáveis `ocasiao` e `estacao` para todas as peças; `PecaVestuario.adequada(...)` também representa esta regra. Uma peça pode estar associada a várias ocasiões e estações, desde que inclua ambas as selecionadas no pedido. Os testes em `TestesSimples` verificam separadamente uma ocasião errada e uma estação errada.

## 1. Ideia geral

O programa tem três partes bem separadas:

- **Modelo:** representa os objetos reais do problema, como utilizadores, peças, outfits e combinações.
- **Controladores:** recebem pedidos dos menus e verificam as regras de negócio.
- **Interface:** mostra menus e lê as respostas na consola.

Além disso, há uma classe pequena para a **persistência** (guardar e carregar os dados).

O programa começa em `Main.main()`, carrega os dados, cria o objeto `AssistenteDigital` e chama `InterfaceConsola.executar()`. A interface decide qual o menu a apresentar de acordo com o tipo de utilizador que iniciou sessão.

### Exemplo da sequência de um pedido de outfit

1. `InterfaceConsola.solicitarOutfit` pede a ocasião e a estação e solicita confirmação (UC6).
2. `OutfitController.solicitar` recebe o utilizador, a ocasião e a estação.
3. O controlador percorre as `CombinacaoPossivel` definidas pelo administrador (UC15).
4. `GeradorOutfits.procurarPecas` procura uma peça disponível e adequada para **cada tipo de roupa** necessário à combinação.
5. Se faltar um tipo, esse conjunto não serve. O gerador tenta a próxima combinação.
6. Se nenhuma combinação resultar, o método devolve `null` e a interface explica que não foi possível gerar um outfit. **Nenhum pedido falhado é registado.**
7. Quando encontra uma combinação válida, são criados `Outfit` e `PedidoOutfit`, ligados ao utilizador.
8. `FicheiroDados.guardar` grava os dados atualizados no ficheiro local.

**Exemplo concreto:** o administrador define `Calças + Camisola`. Para o Inverno e Trabalho, o utilizador precisa de ter uma peça disponível de cada um desses tipos, ambas registadas como adequadas à estação e ocasião. Se só tiver calças, não se forma um outfit. Não se utilizam partes do corpo para decidir a combinação.

## 2. Explicação das classes principais

| Classe | Responsabilidade |
|---|---|
| `Main` | Arranca a aplicação e recebe a língua da interface. |
| `AssistenteDigital` | Mantém o acesso aos dados e cria os controladores. |
| `DadosAplicacao` | Junta listas de utilizadores, tipos de roupa, cores, estações, ocasiões e combinações. Também cria os catálogos predefinidos. |
| `Utilizador` | Guarda os dados da conta, o guarda-roupa e o histórico de pedidos; inclui o papel de administrador. |
| `GuardaRoupa` | Contém as peças de um utilizador e verifica se um nome já existe. |
| `PecaVestuario` | Representa nome, tipo, uma ou mais cores, estações, ocasiões e disponibilidade. |
| `TipoRoupa`, `Cor`, `Estacao`, `Ocasiao` | Elementos dos quatro catálogos, com valores iniciais que o administrador pode aumentar. |
| `ElementoCatalogo` | Superclasse com `id` e `designacao`, para evitar repetir estes atributos quatro vezes. |
| `CombinacaoPossivel` | Regra definida pelo administrador: nome e pelo menos dois tipos distintos. |
| `PedidoOutfit` | Regista a data, a ocasião, a estação e o outfit gerado. |
| `Outfit` | Guarda as peças selecionadas, a combinação utilizada e a avaliação (se existir). |
| `Avaliacao` | Guarda a nota e a data de avaliação, atualizando a nota anterior. |
| `AutenticacaoController` | Regista utilizadores e verifica o email e a palavra-passe. |
| `AdministracaoController` | Define catálogos e combinações possíveis. |
| `GuardaRoupaController` | Adiciona, edita, remove e marca peças como indisponíveis. |
| `OutfitController` | Solicita outfits, regista avaliações e consulta o histórico do próprio utilizador. |
| `GeradorOutfits` | Procura peças para cumprir uma regra de combinação. |
| `InterfaceConsola` | Controla menus, confirmações e apresentação dos resultados. |
| `Consola` | Lê respostas, números e seleções múltiplas. |
| `Textos` | Lê frases dos ficheiros das três línguas. |
| `Seguranca` | Calcula um hash da palavra-passe e um sal diferente para cada conta. |
| `FicheiroDados` | Guarda e carrega um objeto Java no ficheiro binário. |

No **modelo de domínio** do relatório aparecem `Cliente`, `Administrador` e `AssistenteDigital`. No código, para simplificar, tanto o cliente como o administrador são objetos `Utilizador`, distinguidos pelo atributo booleano `administrador`. Os controladores de casos de uso também foram agrupados por assunto. A finalidade funcional permanece a mesma, mas os diagramas de design detalhados não são reproduzidos classe a classe na implementação simplificada.

## 3. Mapa entre casos de uso e código

| UC | Onde se implementa |
|---|---|
| UC1 Adicionar peça | `InterfaceConsola.adicionarPeca` → `GuardaRoupaController.adicionar` |
| UC2 Remover peça | `InterfaceConsola.removerPeca` → `GuardaRoupaController.remover` |
| UC3 Editar peça | `InterfaceConsola.editarPeca` → `GuardaRoupaController.editar` |
| UC4 Indisponibilidade | `InterfaceConsola.marcarIndisponivel` → `GuardaRoupaController.marcarIndisponivel` |
| UC5 Consultar guarda-roupa | `InterfaceConsola.consultarGuardaRoupa` → `GuardaRoupa.getPecas` |
| UC6 Solicitar outfit | `InterfaceConsola.solicitarOutfit` → `OutfitController.solicitar` → `GeradorOutfits.procurarPecas` |
| UC7 Definir cores | `InterfaceConsola.definirCatalogo("cor")` → `AdministracaoController.adicionarCatalogo` |
| UC8 Definir ocasiões | `InterfaceConsola.definirCatalogo("ocasiao")` → `AdministracaoController.adicionarCatalogo` |
| UC9 Definir estações | `InterfaceConsola.definirCatalogo("estacao")` → `AdministracaoController.adicionarCatalogo` |
| UC10 | **Removido do relatório e do programa.** |
| UC11 Registar utilizador | `InterfaceConsola.registarUtilizador` → `AutenticacaoController.registar` |
| UC12 Definir tipos de roupa | `InterfaceConsola.definirCatalogo("tipo")` → `AdministracaoController.adicionarCatalogo` |
| UC13 Avaliar outfit | `InterfaceConsola.avaliarOutfit` → `OutfitController.avaliar` → `Outfit.avaliar` |
| UC14 Histórico | `InterfaceConsola.consultarHistorico` → `OutfitController.consultarHistorico` |
| UC15 Definir combinações | `InterfaceConsola.definirCombinacao` → `AdministracaoController.definirCombinacao` |

## 4. Programação Orientada por Objetos utilizada

**Encapsulamento:** os atributos são geralmente `private` e acedidos através de métodos `get...()`. Por exemplo, a interface não muda diretamente a disponibilidade de uma peça: chama o controlador, que chama `PecaVestuario.marcarIndisponivel()`.

**Construtores:** `new PecaVestuario(...)` cria uma peça com os seus valores iniciais; o construtor exige nome, tipo, cores, estações e ocasiões.

**Composição:** cada `Utilizador` tem um `GuardaRoupa`, e cada `GuardaRoupa` contém uma lista de `PecaVestuario`.

**Herança:** `TipoRoupa`, `Cor`, `Estacao` e `Ocasiao` herdam os atributos comuns da classe abstrata `ElementoCatalogo`.

**Coleções `ArrayList`:** usadas para guardar vários objetos, por exemplo as peças de um guarda-roupa. O ciclo `for` permite percorrer essas listas.

**Controladores:** fazem a ligação entre a interface e os objetos de domínio. É uma aplicação simples da separação de responsabilidades discutida no relatório (RDD/GRASP).

**Validações:** antes de guardar uma peça, verifica-se se os catálogos são válidos e se já existe outra peça com o mesmo nome no guarda-roupa. Antes de registar uma combinação, verifica-se se os tipos existem e não se repetem. Só após a confirmação o programa grava a alteração.

## 5. Catálogos iniciais (sem características adicionais)

Os quatro catálogos são preenchidos no construtor `DadosAplicacao()` através do método `garantirCatalogosPredefinidos()`:

- **Cores (7):** Preto, Branco, Azul, Vermelho, Verde, Amarelo e Cinzento.
- **Ocasiões (5):** Trabalho, Aulas, Jantar, Casamento e Dia a dia.
- **Estações (4):** Primavera, Verão, Outono e Inverno.
- **Tipos de roupa (5):** Calças, Camisola, Casaco, T-shirt e Vestido.

Esse método percorre cada lista e só adiciona um valor se ainda não existir uma designação igual (ignorando maiúsculas/minúsculas). Por isso, voltar a carregar os dados não cria duplicados. O método `FicheiroDados.carregar()` chama essa inicialização também para ficheiros antigos.

O administrador continua a poder adicionar novos valores com `AdministracaoController.adicionarCatalogo()`. Esta alteração **não** reintroduz o UC10 nem tamanho, padrão ou outras características de uma peça.

**Exemplo:** uma utilizadora nova pode adicionar `Camisola azul`, escolhendo `Camisola`, `Azul`, `Inverno` e `Trabalho` dos catálogos existentes. Para solicitar um outfit, o administrador ainda terá de definir pelo menos uma regra de combinação de tipos de roupa.

## 6. Como funciona `Serializable` (passo a passo)

### `Serializable` não é uma base de dados

`Serializable` é uma interface do Java. Quando uma classe implementa `Serializable`, está a indicar ao Java que os seus objetos podem ser convertidos numa sequência de bytes, desde que os seus atributos também sejam serializáveis.

Exemplo reduzido:

```java
public class DadosAplicacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<Utilizador> utilizadores = new ArrayList<>();
}
```


O `serialVersionUID` é um identificador da versão da classe usado durante a desserialização. É comum atribuir-lhe um valor fixo. Isso **não garante** que futuras mudanças incompatíveis na classe permitam ler todos os ficheiros antigos.

### Guardar: `ObjectOutputStream`

No ficheiro `FicheiroDados.java`, o método `guardar()` executa, essencialmente:

```java
try (ObjectOutputStream saida =
         new ObjectOutputStream(new FileOutputStream("dados/vestuario.dat"))) {
    saida.writeObject(dados);
}
```

1. `FileOutputStream` abre/cria o ficheiro para escrita.
2. `ObjectOutputStream` transforma o objeto e os objetos relacionados em bytes.
3. `writeObject(dados)` escreve os bytes no disco.
4. O `try (...)` fecha automaticamente o ficheiro, mesmo se surgir um erro.

A gravação inclui os objetos alcançáveis a partir de `DadosAplicacao`: por exemplo, `Utilizador → GuardaRoupa → PecaVestuario` e `Utilizador → PedidoOutfit → Outfit → Avaliacao`. O Java conserva as referências comuns entre os objetos durante esse processo.

### Carregar: `ObjectInputStream`

```java
try (ObjectInputStream entrada =
         new ObjectInputStream(new FileInputStream("dados/vestuario.dat"))) {
    return (DadosAplicacao) entrada.readObject();
}
```

1. `FileInputStream` abre o ficheiro.
2. `ObjectInputStream` lê os bytes.
3. `readObject()` reconstrói os objetos na memória.
4. O *cast* `(DadosAplicacao)` indica o tipo esperado do objeto lido.
5. O programa continua a trabalhar com as mesmas classes Java, como se as listas tivessem sido criadas nessa execução.

Se ainda não existir ficheiro, `carregar()` cria um `new DadosAplicacao()` com os quatro catálogos iniciais preenchidos e guarda-o. Se já houver ficheiro de uma versão anterior, mantém os utilizadores e peças existentes, mas acrescenta valores predefinidos em falta sem duplicações.

### Onde fica o ficheiro?

Na raiz do projeto, dentro da pasta `dados`, com o nome `vestuario.dat`. É criado logo na primeira execução com os catálogos iniciais e depois é atualizado **depois de cada operação com sucesso**. Não é um ficheiro de texto, não contém tabelas SQL e não pode ser consultado com um gestor de bases de dados tradicional.

A criação de um `Serializable` não provoca gravação automática: é indispensável chamar `guardar()` e depois, na próxima execução, `carregar()`.

**Não apagues** esse ficheiro sem uma cópia se quiseres conservar os utilizadores e outfits. Se alterares a estrutura das classes de forma incompatível, um ficheiro de uma versão anterior poderá deixar de ser legível.

## 7. Segurança e limitações técnicas

A classe `Seguranca` cria um sal aleatório para cada conta e calcula um hash SHA-256 da palavra-passe. Ao fazer login, a palavra-passe introduzida é transformada novamente num hash com o sal desse utilizador, para comparar os resultados. O ficheiro `.dat` **não guarda a palavra-passe original**.

Isto **não significa** que o armazenamento esteja encriptado, nem que o sistema garanta segurança de produção. Há proteção didática do login e separação de dados por utilizador, mas os dados pessoais continuam acessíveis a quem tiver acesso ao ficheiro. Para a produção, usar uma base de dados com controlo de acessos, encriptação e uma função apropriada de derivação de palavras-passe (por exemplo PBKDF2 ou Argon2).

A aplicação permite interface em português, inglês e francês. O idioma é escolhido no arranque, sem criar o caso de uso «Mudar idioma». Não foi implementado o UC10, nem tamanho, padrão, características adicionais ou associações de peças a partes do corpo.

A escolha da primeira regra e da primeira peça compatível é deliberadamente simples. As exigências do relatório relativas a desempenho precisam de testes específicos; **não são automaticamente satisfeitas** pelo facto de o programa funcionar.

## 8. Testar e explicar na apresentação

Executa `TestesSimples.main()` no IntelliJ e explica três situações:

1. **Combinação válida:** duas peças adequadas, uma de cada tipo exigido; `OutfitController.solicitar` devolve um `PedidoOutfit`.
2. **Combinação impossível:** falta uma peça, está indisponível ou não corresponde à ocasião/estação; o método devolve `null` e não regista pedido.
3. **Avaliação e histórico:** o utilizador avalia uma recomendação, sai, inicia novamente a aplicação e consulta a nota guardada.

É especialmente importante conseguir justificar por que razão a geração de outfits está em `GeradorOutfits` e não em `InterfaceConsola`: o primeiro conhece a regra do problema e a segunda deve limitar-se à interação com o utilizador.
