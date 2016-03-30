ann: finalAnn = if useMIF then seq_search( [ int_search(mifSatisfied, input_order, indomain_min, complete),  custSearch ]) else custSearch endif;
