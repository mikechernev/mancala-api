package com.mikechernev.mancala.api.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.mongodb.morphia.annotations.Embedded;

/**
 * Created by mike on 24/04/16.
 */
@Embedded
public class Board {
    private int[] currentState;

    public Board() {
        this.resetBoard();
    }

    public Board(int[] state) {
        this.currentState = state;
    }

    public int[] getState() {
        return this.currentState;
    }

    @JsonIgnore
    public int getHostStones() {
        return this.currentState[BoardSettings.HOST_KALAH];
    }

    @JsonIgnore
    public int getGuestStones() {
        return this.currentState[BoardSettings.GUEST_KALAH];
    }

    @JsonIgnore
    public boolean isHostOver() {
        for (int i = 0; i < BoardSettings.HOST_KALAH; i++) {
            if (this.currentState[i] > 0) {
                return false;
            }
        }

        return true;
    }

    @JsonIgnore
    public boolean isGuestOver() {
        for (int i = BoardSettings.HOST_KALAH + 1; i < BoardSettings.GUEST_KALAH; i++) {
            if (this.currentState[i] > 0) {
                return false;
            }
        }

        return true;
    }

    public boolean isHostPit(int pit) {
        return pit >= 0 && pit < BoardSettings.HOST_KALAH;
    }

    public boolean isGuestPit(int pit) {
        return pit > BoardSettings.HOST_KALAH && pit < BoardSettings.GUEST_KALAH;
    }

    public void applyMove(Move move) {
        int pit = move.getPit();
        int homeKalah = this.getHomeKalahFromPit(pit);
        int endPit = this.distributeStones(pit, homeKalah);

        if (this.isCaptureStone(endPit, homeKalah)) {
            this.captureStones(endPit, homeKalah);
        }

        move.setEndPit(endPit);
    }

    public boolean isMoveValid(Move move) {
        int pit = move.getPit();

        return this.isValidPit(pit) && this.currentState[pit] > 0;
    }

    public boolean isEndPitHomeKalah(Move move) {
        return this.getHomeKalahFromPit(move.getPit()) == move.getEndPit();
    }

    public void collectStones() {
        for (int i = 0; i < BoardSettings.PITS; i++) {
            // Ignore kalahs
            if (i == BoardSettings.HOST_KALAH || i == BoardSettings.GUEST_KALAH) {
                continue;
            }

            int index = i < BoardSettings.HOST_KALAH ? BoardSettings.HOST_KALAH : BoardSettings.GUEST_KALAH;
            this.currentState[index] += this.currentState[i];
            this.currentState[i] = 0;
        }
    }

    // Package public
    void resetBoard() {
        this.currentState = BoardSettings.INITIAL_STATE.clone();
    }

    private int distributeStones(int startingPit, int homeKalah) {
        int stones = this.currentState[startingPit];
        int opponentsKalah = this.getOpponentKalah(homeKalah);

        this.currentState[startingPit] = 0;

        int jumps = 0;
        for (int i = 1; i <= stones; i++) {
            int pitIndex = this.realIndex(startingPit + jumps + i);

            // Ignore opponent's kalah
            if (pitIndex == opponentsKalah) {
                pitIndex = this.realIndex(pitIndex + 1);
                jumps++;
            }

            this.currentState[pitIndex]++;
        }

        return this.realIndex(startingPit + jumps + stones);
    }

    private int getHomeKalahFromPit(int pit) {
        if (pit <= BoardSettings.HOST_KALAH) {
            return BoardSettings.HOST_KALAH;
        }

        return BoardSettings.GUEST_KALAH;
    }

    private int getOpponentKalah(int homeKalah) {
        return this.realIndex(homeKalah + BoardSettings.OFFSET);
    }

    private int realIndex(int index) {
        return index % BoardSettings.PITS;
    }

    private boolean isCaptureStone(int endPit, int homeKalah) {
        return this.currentState[endPit] == 1 && this.isOwnPit(endPit, homeKalah);
    }

    private boolean isOwnPit(int pit, int homeKalah) {
        if (homeKalah == BoardSettings.HOST_KALAH) {
            return pit < BoardSettings.HOST_KALAH;
        }

        return pit > BoardSettings.HOST_KALAH && pit < BoardSettings.GUEST_KALAH;
    }

    private void captureStones(int endPit, int homeKalah) {
        int capturedPit = this.getCapturedPit(endPit);

        if (this.currentState[capturedPit] == 0) {
            return;
        }

        this.currentState[homeKalah] += this.currentState[capturedPit] + this.currentState[endPit];
        this.currentState[capturedPit] = 0;
        this.currentState[endPit] = 0;
    }

    private boolean isValidPit(int pit) {
        return pit >= 0 && pit < BoardSettings.PITS &&
               pit != BoardSettings.HOST_KALAH && pit != BoardSettings.GUEST_KALAH;
    }

    private int getCapturedPit(int endPit) {
        // Number of pits excluding the kalahas
        return BoardSettings.PITS - 2 - endPit;
    }
}
