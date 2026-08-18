package kr.hahaha98757.killchain.common

object PacketCodec {
    fun encode(packet: Packet) = when (packet) {
        is MessagePacket -> "message;${packet.message}"
        is NamePacket -> "signal;Name:${packet.name}"
        is AcceptPacket -> "signal;Name:Accept"
        is DuplicatePacket -> "signal;Name:Duplicate"
        is ListPacket -> "signal;List"
        is PortPacket -> "signal;Port"
        is TestPacket -> "signal;Test:${packet.sender}"
        is KillPacket -> "signal;Kill:${packet.sender}"
        is PingPacket -> "signal;Ping"
        is PongPacket -> "signal;Pong"
        is LeavePacket -> "signal;Leave:${packet.client}"
        is ClosePacket -> "signal;Close"
    }

    fun decode(data: String): Packet? {
        val format = data.substringBefore(";")
        val args = data.substringAfter(";").split(":")

        if (format == "message") return MessagePacket(args.joinAfter(0))
        if (format != "signal") return null

        val signal = args[0]
        val signalArgs = args.drop(1)

        return when (signal) {
            "Name" -> when (signalArgs[0]) {
                "Send" -> NamePacket(signalArgs[1])
                "Accept" -> AcceptPacket
                "Duplicate" -> DuplicatePacket
                else -> null
            }
            "List" -> ListPacket
            "Port" -> PortPacket
            "Test" -> TestPacket(signalArgs[0])
            "Kill" -> KillPacket(signalArgs[0])
            "Ping" -> PingPacket
            "Pong" -> PongPacket
            "Leave" -> LeavePacket(signalArgs[0])
            "Close" -> ClosePacket
            else -> null
        }
    }

    private fun Collection<String>.joinAfter(index: Int, delimiter: String = ":") = this.drop(index).joinToString(delimiter)
}