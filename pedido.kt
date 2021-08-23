import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {

    val listaClientes = mutableListOf<Cliente>()
    val listaPedidos = mutableListOf<Pedido>()

    

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
        val fw = FileWriter(File("clientes.txt"), true)
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
                "Data e hora do pedido: $dataPedidoString"

    }

    open fun gravaEmArquivo(){
        val fw = FileWriter(File("pedidos.txt"), true)
        fw.append("pedidonormal-$numero-$preco-$dataPedidoString-${cliente.getNome()}\n")
        fw.close()
    }
}

class PedidoExpresso(
    numero: Int,
    preco: Double,
    cliente: Cliente,
    dataPedido: LocalDateTime,
    private val dataEntrega: LocalDateTime) : Pedido(numero, preco*1.2, cliente, dataPedido)
{
    private val dataEntregaString = dataEntrega.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

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
        val fw = FileWriter(File("pedidos.txt"), true)
        fw.append("pedidoexpresso-${numero}-$preco-$dataPedidoString-${cliente.getNome()}-${dataEntregaString}\n")
        fw.close()
    }
}

fun leArquivoCliente(listaClientes: MutableList<Cliente>) {
    val fr = FileReader(File("clientes.txt"))
    val objetoEmString = fr.readLines()
    fr.close()

    for(item in objetoEmString) {
        if(item.startsWith("cliente-")) {

            var indiceComeco = item.indexOf("-") + 1
            var indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
            val nome = item.subSequence(indiceComeco, indiceFim).toString()

            indiceComeco = indiceFim + 1
            indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
            val endereco = item.subSequence(indiceComeco, indiceFim).toString()

            indiceComeco = indiceFim + 1
            val numeroString = item.subSequence(indiceComeco, item.lastIndex + 1).toString()
            val numero = numeroString.toInt()

            listaClientes.add(Cliente(nome, endereco, numero))
        }
    }
}

fun leArquivoPedido(listaClientes: MutableList<Cliente>, listaPedidos: MutableList<Pedido>) {
    val fr = FileReader(File("pedidos.txt"))
    val objetoEmString = fr.readLines()
    fr.close()

    for(item in objetoEmString) {
        if (item.startsWith("pedido")) {

            var indiceComeco = item.indexOf("-") + 1
            var indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
            val numeroPedido = (item.subSequence(indiceComeco, indiceFim).toString()).toInt()

            indiceComeco = indiceFim + 1
            indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
            val preco = (item.subSequence(indiceComeco, indiceFim).toString()).toDouble()

            indiceComeco = indiceFim + 1
            indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
            val dataPedidoString = item.subSequence(indiceComeco, indiceFim).toString()
            val dataPedido: LocalDateTime = criaData(dataPedidoString)

            val nomeCliente: String
            if (item.startsWith("pedidoexpresso")) {
                indiceComeco = indiceFim + 1
                indiceFim = item.indexOf(string = "-", startIndex = indiceComeco)
                nomeCliente = item.subSequence(indiceComeco, indiceFim).toString()
                val cliente: Cliente = pegaCliente(listaClientes, nomeCliente)

                indiceComeco = indiceFim + 1
                val dataEntregaString = (item.subSequence(indiceComeco, item.lastIndex + 1).toString())
                val dataEntrega: LocalDateTime = criaData(dataEntregaString)

                listaPedidos.add(PedidoExpresso(numeroPedido,preco,cliente,dataPedido,dataEntrega))
            } else {
                indiceComeco = indiceFim + 1
                nomeCliente = item.subSequence(indiceComeco, item.lastIndex + 1).toString()
                val cliente: Cliente = pegaCliente(listaClientes, nomeCliente)

                listaPedidos.add(Pedido(numeroPedido,preco,cliente,dataPedido))
            }
        }
    }
}

fun pegaCliente(listaClientes: MutableList<Cliente>, nomeCliente: String): Cliente {

    for(cliente in listaClientes){
        if (nomeCliente == cliente.getNome()){
             return cliente
        }
    }
     return Cliente(nomeCliente,"Não informado", 0)
}

fun criaData(dataString: String): LocalDateTime {

    val dia = (dataString.subSequence(0, 2).toString()).toInt()

    val mes = if(dataString[3] == '0'){
        dataString[4].toString().toInt()
    } else{
        (dataString.subSequence(3, 5).toString()).toInt()
    }

    val ano = (dataString.subSequence(6, 10).toString()).toInt()

    val hora = if(dataString[11] == '0'){
        dataString[12].toString().toInt()
    } else{
        (dataString.subSequence(11, 13).toString()).toInt()
    }

    val minuto = if(dataString[14] == '0'){
        dataString[15].toString().toInt()
    } else{
        (dataString.subSequence(14, 16).toString()).toInt()
    }

    return LocalDateTime.of(ano, mes, dia, hora, minuto)
}

fun gravaArquivos(listaClientes: MutableList<Cliente>, listaPedidos: MutableList<Pedido>){

    for(cliente in listaClientes) {
        cliente.gravaEmArquivo()
    }

    for(pedido in listaPedidos) {
        pedido.gravaEmArquivo()
    }
}



