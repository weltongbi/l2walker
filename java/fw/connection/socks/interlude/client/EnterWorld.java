package fw.connection.socks.interlude.client;

import fw.connection.socks.interlude.L2GameSocksClientPacket;

/**
 * Created by Мы on 10.08.14.
 */
public class EnterWorld extends L2GameSocksClientPacket {

    private static byte[] _enterWorldInterlude = new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xC9,(byte)0xBC,(byte)0xF2,(byte)0xA7,(byte)0x66,(byte)0x5A,(byte)0x0B,(byte)0x98,(byte)0x36,(byte)0xA5,(byte)0xBD,(byte)0x89,(byte)0xED,(byte)0x7F,(byte)0xE4,(byte)0xD7,(byte)0x6B,(byte)0x49,(byte)0xE2,(byte)0x9F,(byte)0xEF,(byte)0x76,(byte)0xEB,(byte)0xCE,(byte)0xA3,(byte)0xFA,(byte)0xF4,(byte)0xBF,(byte)0x0C,(byte)0x64,(byte)0xA3,(byte)0xB4,(byte)0x4F,(byte)0xC1,(byte)0x93,(byte)0x20,(byte)0x6A,(byte)0x1C,(byte)0xBF,(byte)0x86,(byte)0x2B,(byte)0xA6,(byte)0x16,(byte)0x49,(byte)0x3D,(byte)0xAF,(byte)0x55,(byte)0x49,(byte)0xF5,(byte)0xB9,(byte)0x59,(byte)0xA4,(byte)0x24,(byte)0x47,(byte)0x85,(byte)0x6F,(byte)0xB7,(byte)0xF6,(byte)0x98,(byte)0x2F,(byte)0x75,(byte)0xC5,(byte)0x3E,(byte)0xCA,(byte)0x29,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};

    @Override
    public void excecute() {
        writeC(0x03);
        writeB(_enterWorldInterlude);
    }
}