package io.github.jeffdshen.project6857.core.net;

import io.github.jeffdshen.project6857.core.board.*;

import java.util.regex.Pattern;

/**
 * Created by jdshen on 5/7/15.
 */
public class EncodingProtocol {
    final protected static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private final static Pattern PERIOD = Pattern.compile("\\.");
    private final static Pattern SEMICOLON = Pattern.compile(";");
    private final static Pattern COMMA = Pattern.compile(",");


    public static String encodeBytes(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            int x = aByte & 0xFF;
            builder.append(HEX_ARRAY[x >>> 4]);
            builder.append(HEX_ARRAY[x & 0x0F]);
        }
        return builder.toString();
    }

    public static String encodeBoard(Piece[][] board) {
        StringBuilder builder = new StringBuilder();
        for (Piece[] row : board) {
            for (Piece p : row) {
                builder.append(encodePiece(p));
                builder.append(";");
            }
            builder.append(".");
        }
        return builder.toString();
    }

    public static Piece[][] decodeBoard(String s) {
        Pattern rowDelimiter = PERIOD;
        Pattern colDelimiter = SEMICOLON;

        String[] rows = rowDelimiter.split(s);
        Piece[][] board = new Piece[rows.length][];

        for (int i = 0; i < rows.length; i++) {
            String[] cols = colDelimiter.split(rows[i]);
            board[i] = new Piece[cols.length];

            for (int j = 0; j < cols.length; j++) {
                Piece piece = decodePiece(cols[j]);
                board[i][j] = piece;
            }
        }
        return board;
    }

    public static String encodeMove(Move move) {
        return encodeLocation(move.getStart()) + ";" + encodeLocation(move.getEnd());
    }

    public static String encodeLocation(Location loc) {
        return loc.getX() + "," + loc.getY();
    }

    public static Location decodeLocation(String s) {
        Pattern delimiter = COMMA;
        String[] xy = delimiter.split(s);
        if (xy.length != 2) {
            throw new IllegalArgumentException();
        }

        return new Location(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
    }

    public static Move decodeMove(String s) {
        Pattern delimiter = SEMICOLON;
        String[] locs = delimiter.split(s);
        if (locs.length != 2) {
            throw new IllegalArgumentException();
        }

        return new Move(decodeLocation(locs[0]), decodeLocation(locs[1]));
    }

    public static String encodePiece(Piece p) {
        if (p == null) {
            return "null";
        }

        return p.getType().ordinal() + "," + p.getRank().ordinal() + "," + p.getIsMine();
    }

    public static Piece decodePiece(String s) {
        if (s.equals("null")) {
            return null;
        }

        Pattern pieceDelimiter = COMMA;
        String[] typeRank = pieceDelimiter.split(s);
        if (typeRank.length != 3) {
            throw new IllegalArgumentException();
        }

        int type = Integer.parseInt(typeRank[0]);
        int rank = Integer.parseInt(typeRank[1]);
        boolean isMine = Boolean.parseBoolean(typeRank[2]);

        return new Piece(PieceType.values()[type], Rank.values()[rank], isMine);
    }

}
