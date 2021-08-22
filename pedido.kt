import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/* FALTA CRIAR UMA FORMA DE VERIFICAR SE O CLIENTE JÁ EXISTE ATRAVES
DO NOME E CRIAR O OBJETO PEDIDO
 */

fun main() {

    val listaClientes = mutableListOf<Cliente>()
    val listaPedidos = mutableListOf<Pedido>()

//    listaClientes.add(Cliente("Fulano", "Rua A", 9999))
//    listaPedidos.add(Pedido(99999, 500.0, listaClientes[0]))
//
//    listaClientes.add(Cliente("Ciclano", "Rua B", 8888))
//    listaPedidos.add(PedidoExpresso(88888, 500.0, listaClientes[1], LocalDateTime.now().plusDays(1)))


    leArquivo(listaClientes, listaPedidos)
    println(listaClientes)
}

class Cliente(
    private val nome: String,
    private val endereco: String,
    private val telefone: Int
    )
{
    override fun toString(): String {
        return "Cliente: $nome - $endereco - $telefone"
    }

    fun gravaEmArquivo(){
        val fw = FileWriter(File("clientes-pedidos.txt"), true)
        fw.append("cliente-$nome-$endereco-$telefone\n")
        fw.close()
    }

    fun getNome() : String{
        return nome
    }
}

open class Pedido(
    protected var numero: Int,
    protected var preco: Double,
    protected val cliente: Cliente,
    protected val dataPedido: LocalDateTime  = LocalDateTime.now())
{
    protected val dataPedidoString: String = dataPedido.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

    override fun toString(): String {
        return "$cliente\n" +
                "Preço do pedido: $preco\n" +
                "Data e hora do pedido: $dataPedidoString}"

    }

    open fun gravaEmArquivo(){
        val fw = FileWriter(File("clientes-pedidos.txt"), true)
        fw.append("pedidonormal-$numero-$preco-$dataPedidoString-${cliente.getNome()}\n")
        fw.close()
    }
}

class PedidoExpresso(
    numero: Int,
    preco: Double,
    cliente: Cliente,
    private val dataEntrega: LocalDateTime) : Pedido(numero, preco*1.2, cliente)
{

    private fun entregueNoPrazo(): Boolean {
        return (dataEntrega.dayOfMonth == dataPedido.dayOfMonth) and
                (dataEntrega.month == dataPedido.month) and
                (dataEntrega.year == dataPedido.year)
    }

    override fun toString(): String {
        val foiEntregue: String = if(this.entregueNoPrazo()){
            "Entregue dentro do prazo"
        }else{
            "Não foi entregue dentro do prazo"
        }
        return super.toString() + "\n$foiEntregue"
    }

    override fun gravaEmArquivo(){
        val fw = FileWriter(File("clientes-pedidos.txt"), true)
        fw.append("pedidoexpresso-${numero}-$preco-$dataPedidoString-${cliente.getNome()}-${dataEntrega}\n")
        fw.close()
    }
}

fun leArquivo(listaClientes: MutableList<Cliente>, listaPedidos: MutableList<Pedido>) {
    val fr = FileReader(File("clientes-pedidos.txt"))
    val objetoEmString = fr.readLines()
    fr.close()

    for(item in objetoEmString) {
        if(item.startsWith("cliente-")) {

            var indiceComeco = item.indexOf("-") + 1
            var indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
            val nome = item.subSequence(8, indiceFim).toString()

            indiceComeco = indiceFim + 1
            indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
            val endereco = item.subSequence(indiceComeco, indiceFim).toString()

            indiceComeco = indiceFim + 1
            val numeroString = item.subSequence(indiceComeco, item.lastIndex + 1).toString()
            val numero = numeroString.toInt()

            listaClientes.add(Cliente(nome, endereco, numero))
        }else if(item.startsWith("pedido")){

            var indiceComeco = item.indexOf("-") + 1
            var indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
            val numeroPedido = (item.subSequence(indiceComeco, indiceFim).toString()).toInt()

            indiceComeco = indiceFim + 1
            indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
            val preco = (item.subSequence(indiceComeco, indiceFim).toString()).toDouble()


            indiceComeco = indiceFim + 1
            indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
            val dataPedido = item.subSequence(indiceComeco, indiceFim).toString()

            val nomeCliente: String
            if(item.startsWith("pedidoexpresso")) {
                indiceComeco = indiceFim + 1
                indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
                nomeCliente = item.subSequence(indiceComeco, indiceFim).toString()

                indiceComeco = indiceFim + 1
                val dataEntrega = (item.subSequence(indiceComeco, item.lastIndex + 1).toString())
                //criar datas e o objeto
            }else{
                indiceComeco = indiceFim + 1
                nomeCliente = item.subSequence(indiceComeco, item.lastIndex + 1).toString()
                //criar objeto 
            }

        }
    }


}

