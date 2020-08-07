package test;

import java.io.IOException;

import medicalEquip.MeSystem;
import nz.ac.waikato.modeljunit.GreedyTester;
import nz.ac.waikato.modeljunit.Tester;
import nz.ac.waikato.modeljunit.VerboseListener;
import nz.ac.waikato.modeljunit.coverage.CoverageMetric;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;
public class TestOnline {
	public static void main(String[] args) throws IOException {
		// Costruisco un tester
		MeSystem me = new MeSystem();
		Tester tester = new GreedyTester(new AlarmStubModel(false, 0, "LOW",15, 5, 0, me));
		// Costruisco il grafo della mia FSMtester.buildGraph();
		// Setto l'utilizzo della metrica come copertura delle transizioni
		CoverageMetric trCoverage = new TransitionCoverage();tester.addCoverageMetric(trCoverage);
		// Faccio stampare il grafo 
		tester.addListener(new VerboseListener());
		// Genero una suite di test con 20 passi
		tester.generate(20);
		// Messaggio di terminazione
		tester.getModel().printMessage(trCoverage.getName() + " was " + trCoverage.toString());
	}
}