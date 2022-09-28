import java.util.Random;

public class ProducerConsumer {

	static final int N = 100; // constante contendo o tamanho do buffer
	static producer p = new producer(); // instancia de um novo thread produtor
	static consumer c = new consumer(); // instancia de um novo thread consumidor
	static our_monitor mon = new our_monitor(); // instancia de um novo monitor

	public static void main(String args[]) {
		p.start(); // inicia o thread produtor
		c.start(); // inicia o thread consumidor
	}

	static class producer extends Thread {
		public void run() {// o metodo run contem o codigo do thread
			int item;
			while (true) { // laco do produtor
				item = produce_item();
				mon.insert(item);

			}
		}

		private int produce_item() {

			Random random = new Random();

			boolean naoPrimo = true;
			int numeroPrimo = 0;

			while (naoPrimo) {

				int numeroAleatorio = random.nextInt(100);

				int qtdDeDivisoes = 0;
				for (int i = 2; i < numeroAleatorio; i++) {
					if (numeroAleatorio % i == 0) {
						qtdDeDivisoes += 1;
					}
				}

				if (qtdDeDivisoes == 0 && numeroAleatorio > 1) {
					naoPrimo = false;
					numeroPrimo = numeroAleatorio;
				}
			}

			return numeroPrimo;
		} // realmente produz
	}

	static class consumer extends Thread {
		public void run() { // metodo run contem o codigo do thread
			int item;
			while (true) { // laco do consumidor
				item = mon.remove();
				consume_item(item);
			}
		}

		private void consume_item(int item) {
			System.out.println(item);
		}// realmente consome
	}

	static class our_monitor { // este e o monitor
		private int buffer[] = new int[N];
		private int count = 0, lo = 0, hi = 0; // contadores e indices

		public synchronized void insert(int val) {
			if (count == N) {
				go_to_sleep();
				System.out.println("Insert - buffer cheio - va dormor produtor!");
			} // se o buffer estiver cheio, va dormir
			buffer[hi] = val; // insere um item no buffer
			hi = (hi + 1) % N; // lugar para colocar o proximo item
			count = count + 1; // mais um item no buffer agora
			if (count == 1) {
				notify();
				System.out.println("O consumidor tava dormindo - acordaro consumidor!");
			} // se o consumidor estava dormindo, acorde-o
		}

		public synchronized int remove() {
			int val;
			if (count == 0) {
				go_to_sleep();
				System.out.println("O buffer esta vasio - va dormir consumidor!");
			} // se o buffer estiver vazio, va dormir
			val = buffer[lo]; // busca um item no buffer
			lo = (lo + 1) % N; // lugar de onde buscar o proximo item
			count = count - 1; // um item a menos no buffer
			if (count == N - 1) {
				notify();
				System.out.println("Remove - produtor estava dormindo - acordar o produtor!");
			} // se o produtor estava dormindo, acorde-o
			return val;
		}

		private void go_to_sleep() {
			try {
				wait();
			} catch (InterruptedException exc) {
			}
			;
		}
	}
}
