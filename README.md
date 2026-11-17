# Territory Predictive Insights: monitoramento de risco de alagamento

Global Solution 2º/2026 (POO em Java) | Tema: CitizenTech

## Integrantes

| Nome completo | RM |
|---|---|
| Patrick Mansour | 562970 |
| Pietro Mauer | 564345 |
| Samir Assad | 561562 |

## Público beneficiado e decisão apoiada

Moradores de regiões sujeitas a alagamento (o exemplo simulado é a "Zona Sul") e a equipe
da associação de bairro ou da defesa civil que cuida desse território.

A pergunta que o sistema ajuda a responder: *"vai chover forte nas próximas horas, e devemos
avisar moradores ou mobilizar equipe agora?"*. O sistema **não decide nada sozinho**: ele
emite um alerta com justificativa, e a pessoa responsável escolhe a ação.

O território é descrito apenas por região e equipamento (pluviômetro, régua do córrego,
sonda de solo). Nenhum dado pessoal de pessoas reais é usado.

## Funcionalidades

- Leitura de 3 tipos de sensor (chuva, nível do córrego, umidade do solo), simulada com `Random`
- Validação de cada medição e gravação no banco
- Duas estratégias de previsão sobre o histórico do banco: média móvel e comparação com o período anterior
- Alertas em 3 níveis (ATENÇÃO, ALERTA, CRÍTICO) com justificativa: sensor, valor previsto e limite ultrapassado
- Histórico de medições por sensor e de alertas emitidos
- Gerenciamento da população e dos limiares do território
- Simulação de falha de sensor
- Menu de console

## Tecnologias

Java 17, Spring Boot 4.1.2, Spring Data JPA, banco H2, JUnit 5 e Mockito, Maven.

## Como executar

Pré-requisito: JDK 17 ou superior.

```bash
# Rodar o sistema (menu de console)
.\mvnw.cmd spring-boot:run

# Rodar os testes
.\mvnw.cmd test
```

No Linux/macOS, use `./mvnw` no lugar de `.\mvnw.cmd`.

O banco H2 é criado automaticamente em `./data/territorio` na primeira execução (configuração em
`src/main/resources/application.properties`, sem script SQL necessário). Os dados persistem
entre execuções. Para começar do zero, apague a pasta `data/`.

## Estrutura do projeto

```
src/main/java/br/com/fiap/territory_predictive_insights_fev/
├── dominio/       ComponenteTerritorial, PontoMonitoramento, Sensor, SensorChuva,
│                  SensorNivelRio, SensorUmidadeSolo, DadosTerritorio, EstrategiaPrevisao,
│                  PrevisaoMediaMovel, PrevisaoComparacao, Medicao, Alerta, NivelAlerta
├── excecao/       DadosInvalidosException, FonteIndisponivelException
├── persistencia/  MedicaoRepository, AlertaRepository (Spring Data JPA)
├── servico/       ServicoMonitoramento, ConfiguracaoSistema
└── SistemaMonitoramento.java (menu principal)
src/test/java/     MedicaoTest, PrevisaoMediaMovelTest, PrevisaoComparacaoTest,
                   ServicoMonitoramentoTest
```

## Decisões da equipe

- **Spring Boot + JPA** (e não JDBC): menos código repetitivo; o Spring gerencia os recursos do banco.
- **H2 em arquivo**: o histórico precisa sobreviver ao reiniciar o programa.
- **Exceções como `RuntimeException`**: evitam `throws` em toda a cadeia e facilitam o `assertThrows` nos testes.
- **`Medicao` sem setters**: a validação roda no construtor, então não existe medição inválida.
- **Alertas só para chuva**: os limiares (mm/h) só fazem sentido para esse sensor. Nível do córrego e umidade do solo gravam histórico e aparecem no status, mas ainda não geram alerta.
- **Escolha da estratégia sem `if`**: o serviço recebe qualquer `EstrategiaPrevisao` e chama `prever()` (polimorfismo).

## Padrão criacional: Singleton (`ConfiguracaoSistema`)

Os limiares de alerta são uma configuração única do sistema. Com o Singleton (construtor
privado, acesso por `getInstancia()`), o serviço de alertas e a tela de gerenciamento do
território sempre enxergam os mesmos valores. Sem ele, duas instâncias poderiam guardar
limiares diferentes, e o alerta dependeria de quem a criou. A validação
(atenção < alerta < crítico) fica num só lugar.

## Fontes de dados e limitações

- Os valores dos sensores são **simulados** com `Random`, em faixas realistas. Não há integração com sensores ou fontes públicas.
- Os limiares (20, 40 e 60 mm/h) são valores de exemplo, não calibrados por estudo hidrológico.
- As previsões são simples (média e tendência linear). Não consideram relevo, solo saturado nem previsão meteorológica.
- Um sensor por tipo representa a região inteira, o que é uma simplificação.

## Reflexão crítica

**Quando o sistema deve se calar?**
Quando tem pouca leitura guardada no banco. No nosso sistema, a previsão se recusa a rodar se
o histórico for pequeno: a média móvel precisa de pelo menos 3 leituras e a comparação com o
período anterior precisa de 6 (dois períodos de 3). Com 1 ou 2 leituras, qualquer número seria
chute, e um alerta falso faz a pessoa perder a confiança no sistema ou mobilizar gente à toa.
É melhor o sistema dizer "não tenho dados suficientes" do que inventar um valor. Um ponto que
ainda não resolvemos: se o sensor cair, o sistema continua prevendo em cima de leituras
antigas, e o ideal seria avisar que a previsão está desatualizada.

**O que os dados não representam?**
Os valores são sorteados com `Random`, então não têm o comportamento da chuva de verdade, que
vai aumentando e diminuindo ao longo das horas. Por isso a média e a tendência calculadas aqui
mostram o funcionamento do sistema, mas não têm sentido físico. Também usamos um sensor de cada
tipo para a região inteira, e na vida real chove forte num bairro e fica seco no outro. E o
sistema não enxerga o que faz alagar de verdade: relevo, bueiro entupido, solo já encharcado e
previsão do tempo. Os limiares (20, 40 e 60 mm/h) são só exemplos.

**Por que o alerta apoia a decisão da pessoa, em vez de decidir por ela?**
Porque o alerta pode estar errado. Se o sistema avisasse os moradores ou mobilizasse uma equipe
sozinho, um alarme falso geraria custo e desgaste, e um erro mais sério ficaria sem ninguém
responsável. A pessoa que cuida do território sabe coisas que o sistema não sabe: se já está
chovendo forte agora, se aquela rua costuma alagar, se tem equipe disponível. Por isso cada
alerta vem com a justificativa (qual sensor, qual valor previsto e qual limite foi passado), pra
pessoa avaliar e decidir. O próprio menu deixa claro que a ação é do gestor.

## Testes

13 testes automatizados (JUnit 5 + Mockito), padrão AAA:

- Cálculo das duas estratégias de previsão
- `assertThrows` para `DadosInvalidosException` (medição negativa, id vazio, histórico insuficiente)
- Mockito simulando `MedicaoRepository` e `AlertaRepository`: a lógica de alertas é testada sem banco

## Uso de IA

Usamos o Claude (Anthropic) como assistente durante o projeto. Ele sugeriu a estrutura de
classes e escreveu os rascunhos do código, dos testes e deste README (inclusive o rascunho da
reflexão crítica), um passo de cada vez. Também ajudou a resolver erros de configuração do
IntelliJ e do Maven.

A cada etapa, o código foi executado e testado (menu no console, banco e os 13 testes) antes
de cada commit.
