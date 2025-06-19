public class App {
    public static void main(String[] args) throws Exception {
        MatchCards matchCards = new MatchCards();
        matchCards.showHomeFrame();
    }
}
// public class App {
//     public static void main(String[] args) {
//         javax.swing.SwingUtilities.invokeLater(new Runnable() {
//             @Override
//             public void run() {
//                 MatchCards game = new MatchCards();
//                 game.showHomeFrame(); // Mulai dari menu home
//             }
//         });
//     }
// }
