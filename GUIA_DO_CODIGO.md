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
| `Administrador` | Conta que gere catálogos e combinações. |
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
| `Seguranca` | Calcula hashes com sal e cifra ou decifra os dados com AES-GCM. |
| `FicheiroDados` | Serializa, guarda e carrega os dados cifrados e preserva a chave e o ficheiro legado. |

`Utilizador` e `Administrador` são classes independentes. Ambas implementam `Conta`, usada pelo controlador de autenticação. O guarda-roupa e os pedidos pertencem a `Utilizador`.

## 3. Mapa entre casos de uso e código

| UC | Onde se implementa |
|---|---|
| UC1 Adicionar peça | `InterfaceConsola.adicionarPeca` → `GuardaRoupaController.adicionar` |
| UC2 Remover peça | `InterfaceConsola.removerPeca` → `GuardaRoupaController.remover` |
| UC3 Editar peça | `InterfaceConsola.editarPeca` → `GuardaRoupaController.editar` |
| UC4 Indisponibilidade | `InterfaceConsola.marcarIndisponivel` → `GuardaRoupaController.marcarIndisponivel` |
| UC5 Consultar guarda-roupa | `InterfaceConsola.consultarGuardaRoupa` → `GuardaRoupa.getPecas` |
| UC6 Solicitar outfit | `InterfaceConsola.solicitarOutfit` → `OutfitController.solicitar` → `GeradorOutfits.procurarOutfit` |
| UC7 Definir cores | `InterfaceConsola.definirCatalogo("cor")` → `AdministracaoController.adicionarCatalogo` |
| UC8 Definir ocasiões | `InterfaceConsola.definirCatalogo("ocasiao")` → `AdministracaoController.adicionarCatalogo` |
| UC9 Definir estações | `InterfaceConsola.definirCatalogo("estacao")` → `AdministracaoController.adicionarCatalogo` |
| UC10 | **Removido do relatório e do programa.** |
| UC11 Registar utilizador | `InterfaceConsola.registarUtilizador` → `AutenticacaoController.registar` |
| UC12 Definir tipos de roupa | `InterfaceConsola.definirCatalogo("tipo")` → `AdministracaoController.adicionarCatalogo` |
| UC13 Avaliar outfit | `InterfaceConsola.avaliarOutfit` → `OutfitController.avaliar` → `Outfit.avaliar` |
| UC14 Histórico | `InterfaceConsola.consultarHistorico` → `OutfitController.consultarHistorico` |
| UC15 Definir combinações de tipos | `InterfaceConsola.definirCombinacao` → `AdministracaoController.definirCombinacao` |
| UC16 Definir combinações de cores | `InterfaceConsola.definirCombinacaoCores` → `AdministracaoController.definirCombinacaoCores` |

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

### Guardar e carregar

`FicheiroDados.guardar()` transforma o objeto `DadosAplicacao` e os objetos relacionados em bytes com `ObjectOutputStream`. Em seguida, cifra esses bytes e grava `dados/vestuario.dat.enc`. A chave fica em `dados/vestuario.dat.key` e é reutilizada nas gravações seguintes.

`FicheiroDados.carregar()` decifra o ficheiro e reconstrói os objetos com `ObjectInputStream`. O Java conserva as referências comuns durante a serialização. Na primeira abertura, se existir apenas o ficheiro antigo `dados/vestuario.dat`, o programa lê os dados, converte a conta administrativa e cria a cópia cifrada sem modificar o original. Os valores iniciais em falta são acrescentados sem duplicações.

Para recuperar os dados, conserva uma cópia do ficheiro cifrado e da chave correspondente. Restringe o acesso à chave e a qualquer ficheiro legado.

## 7. Segurança e limitações técnicas

A classe `Seguranca` cria um sal aleatório para cada conta e calcula um hash SHA-256 da palavra-passe. Ao fazer login, a palavra-passe introduzida é transformada novamente num hash com o sal desse utilizador, para comparar os resultados. Os ficheiros guardam apenas o hash e o sal.

O AES-GCM protege o conteúdo do ficheiro ativo e deteta alterações aos dados cifrados. Quem tiver o ficheiro e a chave pode ler os dados. O ficheiro legado preservado continua sem cifragem.

A interface permite mudar entre português, inglês e francês no menu principal. As designações predefinidas são traduzidas na apresentação, e os elementos sem tradução conservam a designação original.

O gerador usa regras aprovadas para tipos e cores, prefere até três cores distintas e sorteia entre peças equivalentes. As exigências do relatório relativas a desempenho precisam de testes específicos.

## 8. Testar e explicar na apresentação

Executa `TestesSimples.main()` no IntelliJ e explica três situações:

1. **Combinação válida:** duas peças adequadas, uma de cada tipo exigido; `OutfitController.solicitar` devolve um `PedidoOutfit`.
2. **Combinação impossível:** falta uma peça, está indisponível ou não corresponde à ocasião/estação; o método devolve `null` e não regista pedido.
3. **Avaliação e histórico:** o utilizador avalia uma recomendação, sai, inicia novamente a aplicação e consulta a nota guardada.

É especialmente importante conseguir justificar por que razão a geração de outfits está em `GeradorOutfits` e não em `InterfaceConsola`: o primeiro conhece a regra do problema e a segunda deve limitar-se à interação com o utilizador.
