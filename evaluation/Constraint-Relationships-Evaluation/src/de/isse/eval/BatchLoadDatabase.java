package de.isse.eval;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import de.isse.conf.MiniBrassConfig;
import de.isse.conf.Solver;
import de.isse.jobs.Job;
import de.isse.jobs.JobResult;
import de.isse.results.MiniBrassResult;

public class BatchLoadDatabase {

	public static void main(String[] args) {
		ReadResults rr = new ReadResults();
		Collection<JobResult> results = rr.readValidResults(EvalConstants.RESULTS_DIR);
		
		Connection c = null;
		Statement s = null;
		
		HashMap<MiniBrassConfig, Integer> keyMap = new HashMap<>(results.size());
		
		int configIndices = 1;
		for(JobResult jr : results){
			if(!keyMap.containsKey(jr.getJob().config)) {
				keyMap.put(jr.getJob().config, configIndices);
				++configIndices;
			}
		}
		
		try{
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(EvalConstants.CONNECTION_STRING);
			System.out.println("Opened DB ");
			
			s = c.createStatement();
			try{ 
				s.executeUpdate("DROP TABLE JobResult; DROP TABLE Solver; DROP TABLE Config;");
			} catch(SQLException se) {
				// just silently ignore this
			}
			s.close();
			
			s = c.createStatement();
			File createStatements = new File("../db/creates.sql");
			Scanner sc = new Scanner(createStatements);
			StringBuilder sb = new StringBuilder();
			while(sc.hasNextLine()) {
				sb.append(sc.nextLine()+"\n");
			}
			sc.close();
			String sql = sb.toString();
			
		
			int res = s.executeUpdate(sql);
			System.out.println("Creating table ... "+res);
			s.close();
			
			// load all solvers into table
			String solverStm = "INSERT INTO Solver (ID, SolverName) VALUES (?, ?);";
			PreparedStatement ps = c.prepareStatement(solverStm);
			for(Solver solver : Solver.values()) {
				ps.setInt(1, solver.ordinal());
				ps.setString(2, solver.toString());
				ps.executeUpdate();
			}
			ps.close();
			
			// now load all the configs 
			solverStm = "INSERT INTO Config (ID, Timeout, SearchType, SPD, MIF, PropRed, LnsProb, LnsIter) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
			ps = c.prepareStatement(solverStm);
			
			for(Entry<MiniBrassConfig, Integer> entry : keyMap.entrySet()) {
				ps.setInt(1, entry.getValue());
				ps.setInt(2, entry.getKey().timeout);
				ps.setInt(3, entry.getKey().search.ordinal());
				ps.setBoolean(4, entry.getKey().useSPD);
				ps.setBoolean(5, entry.getKey().mostImportantFirst);
				ps.setBoolean(6, entry.getKey().propagateRedundant);
				ps.setDouble(7, entry.getKey().lnsProb);
				ps.setInt(8, entry.getKey().lnsIter);
				ps.executeUpdate();
			}
			ps.close();
			
			String insertSql = "INSERT INTO JobResult (Problem,Instance,SolverId,ConfigId,Solved,Optimally,Objective,NoSolutions,ElapsedSecs) "+
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";				    
			ps = c.prepareStatement(insertSql);
			
		    
			// load all results into table
			for(JobResult jr : results) {
				Job j = jr.getJob();
				MiniBrassResult result = jr.getResult();
				int configKey = keyMap.get(j.config);
				ps.setString(1, jr.getJob().problem);
				ps.setString(2, jr.getJob().instance);
				ps.setInt(3, jr.getJob().solver.ordinal());
				ps.setInt(4, configKey);
				ps.setBoolean(5, result.solved);
				ps.setBoolean(6, result.solvedOptimally);
				ps.setInt(7, result.objective);
				ps.setInt(8, result.noSolutions);
				ps.setDouble(9, result.elapsedSeconds);
				ps.addBatch();
			}
			int[] batchRes = ps.executeBatch();
			System.out.println("After insert: "+Arrays.toString(batchRes));
			ps.close();
			c.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
