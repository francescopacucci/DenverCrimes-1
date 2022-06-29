package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.sun.javafx.geom.Edge;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	private Graph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	private List<String> bestStrings;
	
	
	public Model() {
		dao= new EventsDao(); 
	}
	
	public void creaGrafo(String categoria, int mese) {
		grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiunta vertici
		Graphs.addAllVertices(this.grafo, dao.getAllVertici(categoria, mese));
		
		//aggiungo archi
		for(Adiacenza a: dao.getArchi(categoria, mese)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
		}
		
		System.out.println("Grafo creato");
		System.out.println("#VERTICI: "+ this.grafo.vertexSet().size());
		System.out.println("#ARCHI: "+ this.grafo.edgeSet().size());
	}
	
	public List<Adiacenza> getArchiMaggioriPesoMedio(){
		List<Adiacenza> list = new ArrayList<>();
		double pesoTot=0.0;
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			pesoTot += this.grafo.getEdgeWeight(e);
		}
		double media=pesoTot/this.grafo.edgeSet().size();
		
		//ri-scorro tutti gli archi prendendo quelli maggiori della media
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>media) {
				list.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), (int)this.grafo.getEdgeWeight(e)));
			}
		}
		return list;
	}
	
	public List<String> calcolaPercorso(String sorgente, String destinazione){
		bestStrings= new LinkedList<String>();
		List<String> parzialeList = new LinkedList<>();
		parzialeList.add(sorgente);
		cerca(parzialeList, destinazione);
		return bestStrings;
	}

	
	private void cerca(List<String> parzialeList, String destinazione /*int L*/) {
		//condizione di terminazione
		if(parzialeList.get(parzialeList.size()-1).equals(destinazione)) {
			//è la soluzione migliore
			if(parzialeList.size() > bestStrings.size()) {
				bestStrings= new LinkedList<>(parzialeList);
			}
			return;
		}
		
		//scorro i vicini dell'ultimo inserito e provo le varie strade
		for(String v: Graphs.neighborListOf(this.grafo, parzialeList.get(parzialeList.size()-1))) {
			if(!parzialeList.contains(v)) {
			parzialeList.add(v);
			cerca(parzialeList, destinazione);
			parzialeList.remove(parzialeList.size()-1);
		}
		}
	}
	
}


