import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun main() {

    val clienteFulano = Cliente("Fulano", "Rua A", 9999)
    val pedidoFulano = Pedido(99999, 500.0, clienteFulano)

    val clienteCilano = Cliente("Ciclano", "Rua B", 8888)
    val pedidoCiclano = PedidoExpresso(88888, 500.0, clienteCilano, LocalDateTime.now().plusDays(1))

    println(pedidoFulano)
    println()
    println(pedidoCiclano)

    clienteFulano.gravaEmArquivo()
    clienteCilano.gravaEmArquivo()

    pedidoFulano.gravaEmArquivo()
    pedidoCiclano.gravaEmArquivo()
}

class Cliente(
    private val nome: String,
    private val endereco: String,
    private val telefone: Int
    )
{
    override fun toString(): String {
        return "Cliente: $nome"
    }

    fun gravaEmArquivo(){
        val fw = FileWriter(File("clientes-pedidos.txt"), true)
        fw.append("$nome $endereco $telefone\n")
        fw.close()
    }

    fun getNome() : String{
        return nome
    }
}

open class Pedido(
    private var numero: Int,
    private var preco: Double,
    private val cliente: Cliente)
{
    val dataPedido: LocalDateTime  = LocalDateTime.now()
    val dataPedidoString: String = dataPedido.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

    override fun toString(): String {
        return "$cliente\n" +
                "Preço do pedido: $preco\n" +
                "Data e hora do pedido: $dataPedidoString}"

    }

    fun gravaEmArquivo(){
        val fw = FileWriter(File("clientes-pedidos.txt"), true)
        fw.append("$numero $preco $dataPedidoString ${cliente.getNome()}\n")
        fw.close()
    }
}

class PedidoExpresso(
    numero: Int,
    preco: Double,
    cliente: Cliente,
    val dataEntrega: LocalDateTime) : Pedido(numero, preco*1.2, cliente)
{

    fun entregueNoPrazo(): Boolean {
        return (dataEntrega.dayOfMonth == dataPedido.dayOfMonth) and
                (dataEntrega.month == dataPedido.month) and
                (dataEntrega.year == dataPedido.year)
    }

    override fun toString(): String {
        val foiEntregue: String
        if(this.entregueNoPrazo()){
            foiEntregue = "Entregue dentro do prazo"
        }else{
            foiEntregue = "Não foi entregue dentro do prazo"
        }
        return super.toString() + "\n$foiEntregue"
    }
}

