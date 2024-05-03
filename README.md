## Projeto Hotel Java

Este projeto simula um sistema de reserva e controle de quartos em um hotel, utilizando threads em Java. Ele representa as entidades de Quarto, Hóspede, Camareira e Recepcionista, e segue as seguintes regras:

- Os recepcionistas só alocam hóspedes em quartos vagos.
- Cada quarto tem capacidade para até 4 hóspedes e uma única chave.
- Grupos ou famílias com mais de 4 membros são divididos em vários quartos.
- Os hóspedes devem deixar a chave na recepção ao sair do hotel.
- Uma camareira só pode entrar em um quarto vago ou quando os hóspedes não estão no quarto.
- A limpeza dos quartos é feita após a saída dos hóspedes.
- Um quarto vago que passa por limpeza não pode ser alocado para um hóspede novo.
- Caso não haja quartos vagos, o hóspede deve esperar em uma fila até um quarto ficar vago.

### Integrantes do Grupo

- Leonardo Duarte Veiga Ferreira
- Rafael Gomes Parente
- Ruan Arthur Rodrigues Gonçalves
- Yann Soares Guimarães da Silva
