import java.io.*;
import java.util.*;

public class BookSolver {

    ///////////// Input
    int nBooks, nLibrary, nDay;
    Map<Integer, Integer> book2Score = new HashMap<>();
    List<Library> libraries = new ArrayList<>();
    int highscore = 0;
    int H1, H2;

    ///////////// Result
    List<Library> answer = new ArrayList<>();
    boolean[] scannedBook;


    public static void main(String[] args) throws IOException {
        BookSolver b = new BookSolver();
        b.ReadInput();

        b.Solve();

        b.WriteOutput();
    }

    void WriteOutput() throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./resources/out.txt")));

        out.println(answer.size());

        for(int i = 0; i< answer.size(); i++)
        {
            Library curLib = answer.get(i);
            out.printf("%d %d\n", curLib.id, curLib.selectedBooks.size());
            for(int j=0; j<curLib.selectedBooks.size(); j++)
                out.printf("%d ",curLib.selectedBooks.get(j));
            out.println("");
        }

        out.close();
    }


    void Solve() {

        for(int s: book2Score.values())
            H1 = Math.max(H1, s);
        System.out.println("max Book = "+H1);
//        H1 = 4000;
//        H2 = 9;
        //        H1  = 300;
//        H2 = 250;


        System.out.println("H1 = "+H1);


        //Solution1_SortShortestProcessingTime();
        Solution2_SortHighestScoreInRemainingTime();
    }

//    private void Solution1_SortShortestProcessingTime() {
//        int ceil = (int) (Math.ceil(l.nBook / l.nBookPerDay));
//        lScore[i] = l.duration + ceil;
//    }

    private void Solution2_SortHighestScoreInRemainingTime() {
        int remDay = nDay, remLib = libraries.size();

        boolean[] scanned = new boolean[libraries.size()];
        for(int i=0; i<libraries.size(); i++)
            scanned[i] = false;

        scannedBook = new boolean[nBooks];
        for(int i=0; i<nBooks; i++)
            scannedBook[i] = false;

        while(remDay>0 && remLib>0)
        {
            int[] lScore = new int[libraries.size()];
            int bestLibId = -1, highestScore = -1, highestScoreReal = -1;

            for(int i=0; i<libraries.size(); i++) {
                if(scanned[i]==true)
                    continue;
                Library l = libraries.get(i);

                if((l.duration+1)>remDay)
                    continue;

                //lScore[i] =  l.GetScoreForDuration(remDay);
                //lScore[i] =  l.GetScoreForDuration_ConsideringScannedBook(remDay);
                PairInt p=  l.GetScoreForDuration_V3(remDay);
                lScore[i] = p.left;
                if(lScore[i]>highestScore || (lScore[i]==highestScore))
                {
                    highestScore = lScore[i];
                    highestScoreReal = p.right;
                    bestLibId = i;
                }
            }

            // Add best lib to answers
            if(bestLibId==-1)
                break;

            scanned[bestLibId] = true;
            Library bestLib = libraries.get(bestLibId);
            highscore += highestScoreReal;
            //bestLib.nBook = Math.min(bestLib.nBook, (remDay-bestLib.duration)*bestLib.nBookPerDay);
            for(int i=0; i<bestLib.selectedBooks.size(); i++)
                scannedBook[bestLib.selectedBooks.get(i)]=true;
            answer.add(libraries.get(bestLibId));
            remDay -= libraries.get(bestLibId).duration;
            remLib--;
        }

        System.out.println("HighScore: "+highscore);
    }


    void ReadInput() throws IOException {
//        BufferedReader f = new BufferedReader(new FileReader("./resources/a_example.txt"));
//        BufferedReader f = new BufferedReader(new FileReader("./resources/b_read_on.txt"));
//        BufferedReader f = new BufferedReader(new FileReader("./resources/c_incunabula.txt"));
        BufferedReader f = new BufferedReader(new FileReader("./resources/d_tough_choices.txt"));
//        BufferedReader f = new BufferedReader(new FileReader("./resources/e_so_many_books.txt"));
//        BufferedReader f = new BufferedReader(new FileReader("./resources/f_libraries_of_the_world.txt"));
        StringTokenizer st = new StringTokenizer(f.readLine());
        nBooks = Integer.parseInt(st.nextToken());
        nLibrary = Integer.parseInt(st.nextToken());
        nDay = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(f.readLine());
        for(int i=0; i<nBooks; i++)
        {
            int score = Integer.parseInt(st.nextToken());
            book2Score.put(i, score);
        }


        for(int i=0; i<nLibrary; i++)
        {
            st = new StringTokenizer(f.readLine());
            Library l = new Library();
            l.id = i;
            l.nBook = Integer.parseInt(st.nextToken());
            l.duration = Integer.parseInt(st.nextToken());
            l.nBookPerDay = Integer.parseInt(st.nextToken());

            st = new StringTokenizer(f.readLine());
            for(int j=0; j<l.nBook; j++)
                l.books.add(Integer.parseInt(st.nextToken()));
            l.books.sort((book1, book2) -> {
                Integer book1Value = book2Score.get(book1);
                Integer book2Value = book2Score.get(book2);

                return book1Value.compareTo(book2Value) * -1;
            });
            libraries.add(l);
        }
    }


    public class Library{
        public int id, nBook, duration, nBookPerDay;
        public  List<Integer> books = new ArrayList<>();
        public  List<Integer> selectedBooks = new ArrayList<>();

        public int GetScoreForDuration(int d)
        {
            d -= duration;
            int maxPossibleBook = d * nBookPerDay;

            int score = 0;
            for(int i=0; i<maxPossibleBook && i<books.size(); i++)
                score += book2Score.get(books.get(i));

            return score;
        }

        public int GetScoreForDuration_ConsideringScannedBook(int d)
        {
            selectedBooks.clear();
            d -= duration;
            int maxPossibleBook = d * nBookPerDay;

            int score = 0;

            int nNewBooks=0;
            for(int i=0; nNewBooks<maxPossibleBook && i<books.size(); i++)
            {
                if(scannedBook[books.get(i)] == false)
                {
                    score += book2Score.get(books.get(i));
                    selectedBooks.add(books.get(i));
                    nNewBooks++;
                }
            }

            return score;
        }

        public PairInt GetScoreForDuration_V3(int d)
        {
            selectedBooks.clear();
            d -= duration;
            int maxPossibleBook = d * nBookPerDay;

            int score = 0, realScore;

            int nNewBooks=0;
            int i=0;
            for(; nNewBooks<maxPossibleBook && i<books.size(); i++)
            {
                if(scannedBook[books.get(i)] == false)
                {
                    score += book2Score.get(books.get(i));
                    selectedBooks.add(books.get(i));
                    nNewBooks++;
                }
            }

            realScore = score;

            if(nNewBooks==maxPossibleBook)
            {
                while(i<books.size())
                {
                    score -= H2;
                    i++;
                }
            }

//            System.out.print("Score Before: "+score);
            score = score +  ( -1*H1*duration);
//            System.out.print("After: "+score+"\n");

            return new PairInt(score, realScore);
        }
    }


    class PairInt {
        public int left, right;
        PairInt(int l, int r){
            this.left = l;
            this.right = r;
        }
    }
}
