package crossflow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 * @generated NOT
 */
public class CrossflowDiagramMigration_3_3_2020 {

	public static void main(String[] args) throws Exception {

		BufferedReader r = new BufferedReader(new FileReader(new File("crossflow_diagram_migration_strategy_3_3_2020.txt")));

		LinkedList<String> lines = new LinkedList<String>();
		String line = null;
		while ((line = r.readLine()) != null) {
			if (line.trim().length() > 0 && !line.startsWith("//"))
				lines.add(line);
		}
		r.close();

		System.out.println(lines);

		Stream<Path> paths = Files.find(Paths.get("../"), 999, (path, basicFileAttributes) -> {
			File file = path.toFile();
			return !file.isDirectory() && file.getName().contains(".crossflow_diagram");
		});

		//

		Object[] files = paths.toArray();
		
		paths.close();
		
		//

		System.out.println(Arrays.deepToString(files));

		for (Object o : files) {
			File f = ((Path) o).toFile();
			LinkedList<String> modelLines = new LinkedList<String>();
			BufferedReader rr = new BufferedReader(new FileReader(f));
			while ((line = rr.readLine()) != null) {
				modelLines.add(line);
			}
			rr.close();

			BufferedWriter w = new BufferedWriter(new FileWriter(f));

			for (String ss : modelLines) {

				for (String s : lines) {
					//
					String[] split1 = s.split(":");
					String values = split1[1];
					String[] split2 = values.split("->");
					
					String before = split2[0].trim();
					String after = split2[1].trim();

					// System.out.println(s + " :: " + before + " :: " + after);
					ss = ss.replace(before, after);
					//
				}
				w.append(ss+"\n");
			}

			w.close();
		}

		System.out.println("done.");

	}

}
