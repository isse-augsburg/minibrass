ann: mifHeuristic = if choco then int_search(mifSatisfied, input_order, indomain_max, complete) else seq_search( [ int_search(mifSatisfied, input_order, indomain_max, complete),  custSearch ])endif;
ann: finalAnn = if mostImportantFirst then mifHeuristic else custSearch endif;
