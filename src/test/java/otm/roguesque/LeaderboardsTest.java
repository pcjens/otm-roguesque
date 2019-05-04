package otm.roguesque;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import otm.roguesque.net.Leaderboard;
import otm.roguesque.net.LeaderboardClient;
import otm.roguesque.net.LeaderboardEntry;
import otm.roguesque.net.LeaderboardServer;

public class LeaderboardsTest {

    private static final int PORT = Leaderboard.DEFAULT_PORT + 1;
    private LeaderboardServer server;
    private LeaderboardClient client;

    @Before
    public void setup() {
        new Thread(() -> {
            server = new LeaderboardServer(PORT, false, false);
            server.start();
        }).start();
        try {
            new Thread().join();
        } catch (InterruptedException ex) {
            Logger.getLogger(LeaderboardsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        client = new LeaderboardClient("127.0.0.1", PORT);
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void serverResponds() {
        Assert.assertTrue(client.serverResponds());
    }

    @Test
    public void nonExistentServerDoesNotRespond() {
        // Silence the output since this will spew out something to the err.
        PrintStream err = System.err;
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int i) throws IOException {
            }
        }));

        client = new LeaderboardClient("127.0.0.1", PORT + 1);
        Assert.assertFalse(client.serverResponds());

        System.setErr(err);
    }

    @Test
    public void sendingProperEntryWorks() {
        LeaderboardEntry entry = new LeaderboardEntry("BOB", 10);
        String error = client.sendNewEntry(entry);
        Assert.assertNull(error);
        ArrayList<LeaderboardEntry> entries = client.getTopDaily();
        Assert.assertTrue(entries.size() == 1);
        Assert.assertTrue(entries.get(0).getName().equals(entry.getName()));
        Assert.assertTrue(entries.get(0).getScore() == entry.getScore());
    }

    @Test
    public void sendingEntryWithTooLongANameDoesNotWork() {
        LeaderboardEntry entry = new LeaderboardEntry("BOBBY", 10);
        String error = client.sendNewEntry(entry);
        Assert.assertNotNull(error);
        ArrayList<LeaderboardEntry> entries = client.getTopDaily();
        Assert.assertTrue(entries.isEmpty());
    }

    @Test
    public void leaderboardSavingWorks() {
        client.sendNewEntry(new LeaderboardEntry("ACE", 12));
        StringWriter writer = new StringWriter();
        server.saveTo(writer);
        String[] parts = writer.getBuffer().toString().split(";");
        Assert.assertTrue(parts[0].equals("ACE"));
        Assert.assertTrue(parts[1].equals("12"));
    }

    @Test
    public void leaderboardLoadingWorks() {
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(1);
        server.loadFrom(new StringReader("OUO;1;" + dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        ArrayList<LeaderboardEntry> entries = client.getTopDaily();
        Assert.assertFalse(entries.isEmpty());
        Assert.assertTrue(entries.get(0).getName().equals("OUO"));
        Assert.assertTrue(entries.get(0).getScore() == 1);
    }
}
