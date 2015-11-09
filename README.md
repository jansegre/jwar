# JWar

Jogo de [War](https://pt.wikipedia.org/wiki/War) ([Risk](https://en.wikipedia.org/wiki/Risk_(game))) em Java.

Feito como trabalho da cadeira de Laboratório de Programação 2 do Instituo Militar de Engenharia.

## Colocando pra funcionar

Compile e rode, a porta padrão é 8080, pode ser passada pela variável de ambiente PORT.

    mvn package
    java -cp target/classes:"target/dependency/*" com.jansegre.jwar.WebApplication

Existe uma interface de linha de comando (CLI):

    java -cp target/classes:"target/dependency/*" com.jansegre.jwar.Application

## Regras

- [Regras oficiais da Grow](http://www.grow.com.br/uploads/p185601alh15441mm1q3q1mjn1j011.pdf)
- [Mesmas regras só que mais amigáveis](http://regras.net/jogo-war/)

## Contribuindo

Antes de contribuir leia esse README. Aparentemente você já está lendo, isso é um bom sinal.

Leia as regras para entender como o jogo funciona.

Esse projeto usa o [Maven](http://maven.apache.org/), não é necessário ter um conhecimento específico
de Maven, apenas é necessário saber como importar um projeto desse tipo em sua IDE, se você não usar
uma IDE aprenda a usar o Maven e seja feliz.

## Licensa

Este projeto está licenciado sob a [AGPL](http://www.gnu.org/licenses/agpl-3.0.html), uma cópia dessa
se encontra no arquivo [LICENSE.md](LICENSE.md).
