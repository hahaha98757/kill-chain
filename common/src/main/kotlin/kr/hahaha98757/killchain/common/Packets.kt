package kr.hahaha98757.killchain.common

sealed interface Packet
sealed interface SignalPacket: Packet

data class MessagePacket(val message: String): Packet

data class NamePacket(val name: String): SignalPacket
data object AcceptPacket: SignalPacket
data object DuplicatePacket: SignalPacket
data object ListPacket: SignalPacket
data object PortPacket: SignalPacket
data class TestPacket(val sender: String): SignalPacket
data class KillPacket(val sender: String): SignalPacket
data object PingPacket: SignalPacket
data object PongPacket: SignalPacket
data class LeavePacket(val client: String): SignalPacket
data object ClosePacket: SignalPacket