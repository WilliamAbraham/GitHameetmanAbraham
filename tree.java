import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;

public class tree {
    public static void main(String[] args) throws Exception {
        tree treeTest = new tree();
        treeTest.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
        treeTest.add("blob : 01d82591292494afd1602d175e165f94992f6f5f : file2.txt");
        treeTest.add("blob : f1d82236ab908c86ed095023b1d2e6ddf78a6d83 : file3.txt");
        treeTest.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        treeTest.add("tree : e7d79898d3342fd15daf6ec36f4cb095b52fd976");
        treeTest.writeToTree();
        treeTest.remove("file2.txt");
        treeTest.remove("e7d79898d3342fd15daf6ec36f4cb095b52fd976");
        treeTest.writeToTree();
    }

    File treeHoldingFile;
    File treeFile;
    ArrayList<String> list;

    public tree() {
        treeHoldingFile = new File("treeHoldingFile");
        list = new ArrayList<String>();
    }

    public void add(String treeEntry) throws Exception {
        String substring = treeEntry.substring(0, 4);
        if (substring.equals("tree")) {
            String sha = treeEntry.substring(7, treeEntry.length());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).contains(sha)) {
                    throw new Exception("already containts that tree");
                }
            }
        } else if (substring.equals("blob")) {
            String shaFileName = treeEntry.substring(treeEntry.indexOf(":") + 2, treeEntry.length());
            String fileName = shaFileName.substring(shaFileName.indexOf(":") + 2, shaFileName.length());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).contains(fileName)) {
                    throw new Exception("already containts that file");
                }
            }
        }
        list.add(treeEntry);
    }

    public void remove(String treeEntry) {
        boolean removed = false;
        for (int i = 0; i < list.size() && removed == false; i++) {
            if (list.get(i).contains(treeEntry)) {
                list.remove(i);
                removed = true;
            }
        }
    }

    public void writeToTree() throws IOException {
        FileWriter writer = new FileWriter(treeHoldingFile);
        for (int i = 0; i < list.size() - 1; i++) {
            writer.write(list.get(i) + "\n");
        }
        writer.write(list.get(list.size() - 1));
        writer.close();
        String contents = getFileContents(treeHoldingFile);
        String shaString = getSHA(contents);
        if (treeFile != null && treeFile.exists()) {
            treeFile.delete();
        }
        treeFile = new File("./objects/" + shaString);
        FileWriter treeWriter = new FileWriter(treeFile);
        treeWriter.write(contents);
        treeWriter.close();

    }

    public String getFileContents(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file.getPath()));
        StringBuilder contents = new StringBuilder();
        while (reader.ready()) {
            contents.append((char) reader.read());
        }
        reader.close();
        return contents.toString();
    }

    private String getSHA(String fileContents) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(fileContents.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sha1;
    }

    // Used for sha1
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
