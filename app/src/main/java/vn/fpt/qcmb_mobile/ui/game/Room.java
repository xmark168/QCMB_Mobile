package vn.fpt.qcmb_mobile.ui.game;

public class Room {
        public String code;
        public String name;
        public String topic;
        public int currentPlayers;
        public int maxPlayers;
        public String ownerName;
        public boolean isPrivate;
        public long createdAt;

        public Room() {
            this.createdAt = System.currentTimeMillis();
            this.isPrivate = false;
        }

        public Room(String code, String name, String topic, int currentPlayers, int maxPlayers, String ownerName) {
            this.code = code;
            this.name = name;
            this.topic = topic;
            this.currentPlayers = currentPlayers;
            this.maxPlayers = maxPlayers;
            this.ownerName = ownerName;
            this.createdAt = System.currentTimeMillis();
            this.isPrivate = false;
        }

        public boolean isFull() {
            return currentPlayers >= maxPlayers;
        }

        public String getPlayersText() {
            return currentPlayers + "/" + maxPlayers;
        }

}
