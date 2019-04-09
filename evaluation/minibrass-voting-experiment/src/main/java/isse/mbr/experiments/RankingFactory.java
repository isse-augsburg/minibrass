package isse.mbr.experiments;

import java.util.List;
import java.util.Random;

public interface RankingFactory {

	List<Integer> getRanking(Random random);

}