#! /usr/bin/env python
import datetime
from Numberjack import *
from Numberjack.Decomp import PostBinary, PostUnary, PostTernary


# library of flatzinc predicates translated into numberjack constraints


def array_bool_and(x,y):
    if ((type(y) is int) and y != 0):
        return (Sum(x) == len(x))
    elif ((type(y) is int) and y == 0):
        return (Sum(x) != len(x))
    else:
        return (y == (Sum(x) == len(x)))


def array_bool_or(x,y):
    if ((type(y) is int) and y != 0):
        if len(x) == 2:
            return Or(x)
        else:
            return Disjunction(x)
    else:
        return (y == Disjunction(x))

def array_bool_xor(x):
    return ((Sum(x) % 2) == 1)

# cost functions direct access through MiniZinc
# only feasible with toulbar2 
def cost_function_unary(var, costs, costVar):
    return PostUnary(var,costs)

def cost_function_binary(var1, var2, costs, costVar):
    return PostBinary(var1, var2, costs)

def cost_function_ternary(var1, var2, var3, costs, costVar):
    return PostTernary(var1, var2, var3, costs)


def array_int_element(x, y, z):
    # Buggy Workaround, produces invalid values in some optimization cases.
    #aux = Variable(x.lb-1, x.ub-1, "somevar_minus1")
    #return [(z == Element([Variable(e,e,str(e)) if type(e) is int else e for e in y], aux)), (x >= 1), (x <= len(y)), (aux == x - 1)]
    #return [(z == Element([Variable(e,e,str(e)) if type(e) is int else e for e in y], x - 1)), int_le(1,x), int_le(x,len(y))]
    u = set()
    for e in y:
        u = u | set([e] if type(e) is int else range(e.lb, e.ub + 1))
    return [int_le(1,x), int_le(x,len(y)), set_in(z, u)] + [((z == e) | (x != i+1)) for i, e in enumerate(y)]

def array_var_int_element(x,y,z):
    return (array_int_element(x,y,z))

def array_bool_element(x,y,z):
    return (array_int_element(x,y,z))

def array_var_bool_element(x,y,z):
    return (array_var_int_element(x,y,z))

def bool2int(x, y):
    return (x == y)

def bool_and(x, y, z):
    return (And(x, y) if ((type(z) is int) and (z != 0)) else (z == And(x, y)))

def bool_clause(x, y):
    if len(x)>0 and len(y)==0:
      return Disjunction(x)
    elif len(x)==0 and len(y)>0:
      return Disjunction([(e == 0) for e in y])
    elif len(x)>0 and len(y)>0:
      return (Disjunction(x) | Disjunction([(e == 0) for e in y]))
    else:
      return []

def bool_le(x, y):
    return ((x == 0) | (y != 0))

def bool_le_reif(x, y, z):
    return [((x != 0) | (z != 0)), ((y != 0) | (z != 0)), ((x == 0) | (y != 0) | (z == 0))]

def bool_lt(x, y):
    return [(x == 0), (y != 0)]

def bool_lt_reif(x, y, z):
    return [((x == 0) | (z == 0)), ((y != 0) | (z == 0)), ((x != 0) | (y == 0) | (z != 0))]

def bool_not(x, y):
    return [((x == 0) | (y == 0)), ((x != 0) | (y != 0))]

def bool_or(x, y, z):
    return (z == (x | y ))

def bool_xor(x, y, z):
    return (z == (x != y))

def int_eq(x,y):
        '''
    if (type(y) is int) and issubclass(type(x), Expression) and x.is_var() and y >= x.lb and y <= x.ub:
        x.domain_ = None
        x.lb = y
        x.ub = y
        return []
    else:
        '''
        return (x == y)

def int_eq_reif(x,y,z):
    return (z == (x == y))

def bool_eq(x, y):
    return (int_eq(x,y))

def bool_eq_reif(x, y, z):
    return (int_eq_reif(x, y, z))

def int_le(x,y):
        '''
    if (type(y) is int) and issubclass(type(x), Expression) and x.is_var() and y >= x.lb: # and (x.domain_ is None)
        x.ub = min(x.ub, y)
        return []
    elif (type(x) is int) and issubclass(type(y), Expression) and y.is_var() and x <= y.ub: # and (y.domain_ is None)
        y.lb = max(y.lb, x)
        return []
    else:
        '''
        return (x <= y)

def int_le_reif(x,y,z):
    return (z == (x <= y))

def int_lt(x,y):
        '''
    if (type(y) is int) and issubclass(type(x), Expression) and x.is_var() and y > x.lb: # and (x.domain_ is None)
        x.ub = min(x.ub, y-1)
        return []
    elif (type(x) is int) and issubclass(type(y), Expression) and y.is_var() and x < y.ub: # and (y.domain_ is None)
        y.lb = max(y.lb, x+1)
        return []
    else:
        '''
        return (x < y)

def int_lt_reif(x,y,z):
    return (z == (x < y))

def int_ne(x,y):
    return (x != y)

def int_ne_reif(x,y,z):
    return (z == (x != y))

def int_lin_eq(coef,vars,res):
    if ((len(coef) == 1) and (coef[0] == 1)):
       return int_eq(vars[0],res)
    else:
       return (res == Sum(vars,coef))

def bool_lin_eq(coef,vars,res):
    return (int_lin_eq(coef,vars,res))

def int_lin_eq_reif(coef,vars,res,z):
    if ((len(coef) == 1) and (coef[0] == 1)):
       return int_eq_reif(vars[0],res,z)
    else:
       return (z == (res == Sum(vars, coef)))

def int_lin_le(coef,vars,res):
    if ((len(coef) == 1) and (coef[0] == 1)):
       return int_le(vars[0],res)
    else:
       return (res >= Sum(vars,coef))

def bool_lin_le(coef,vars,res):
    return (int_lin_le(coef,vars,res))

def int_lin_le_reif(coef,vars,res,z):
    if ((len(coef) == 1) and (coef[0] == 1)):
       return int_le_reif(vars[0],res,z)
    else:
       return (z == (res >= Sum(vars,coef)))

def int_lin_lt(coef,vars,res):
    if ((len(coef) == 1) and (coef[0] == 1)):
       return int_lt(vars[0],res)
    else:
       return (res > Sum(vars,coef))

def int_lin_lt_reif(coef,vars,res,z):
    if ((len(coef) == 1) and (coef[0] == 1)):
       return int_lt_reif(vars[0],res,z)
    else:
       return (z == (res > Sum(vars,coef)))

def int_lin_ne(coef,vars,res):
    if ((len(coef) == 1) and (coef[0] == 1)):
       return int_ne(vars[0],res)
    else:
       return (res != Sum(vars,coef))

def int_lin_ne_reif(coef,vars,res,z):
    if ((len(coef) == 1) and (coef[0] == 1)):
       return int_ne_reif(vars[0],res,z)
    else:
       return (z == (res != Sum(vars,coef)))

def int_abs(x,y):
    return (y == Abs(x))

def int_div(x,y,z):
    if (x is y): return (z == 1)
    return (z == (x / y))

def int_min(x,y,z):
    if (x is y): return (z == x)
    return ((z == Min([Variable(x,x,str(x)),y])) if (type(x) is int) else ((z == Min([x,Variable(y,y,str(y))])) if (type(y) is int) else (z == Min([x,y]))))

def int_max(x,y,z):
    if (x is y): return (z == x)
    return ((z == Max([Variable(x,x,str(x)),y])) if (type(x) is int) else ((z == Max([x,Variable(y,y,str(y))])) if (type(y) is int) else (z == Max([x,y]))))

def int_mod(x,y,z):
    if (x is y): return (z == 0)
    return (z == (x % y))

def int_plus(x,y,z):
    return (z == (x + y))

def int_times(x,y,z):
    return (z == (x * y))

def set_in(x,dom):
    if (type(x) is int):
        if (not(x in dom)):
            return [Variable(x,x,str(x)) != x]
        else:
            return []
#    return (Disjunction([(x == v) for v in dom]))
    return [(x != v) for v in range(x.get_min(),1+x.get_max()) if (not(v in dom))]
'''
    if (x.domain_ is not None):
        olddom = set(x.domain_)
        newdom = set(dom)
        x.domain_ = list(newdom & olddom)
    else:
        x.domain_ = [e for e in dom if e >= x.lb and e <= x.ub]
    x.domain_.sort()
    x.lb = x.domain_[0]
    x.ub = x.domain_[-1]
    return []
'''

def set_in_reif(x,dom,z):
    return (z == Disjunction([(x == v) for v in dom]))

# specific global constraints for numberjack

def all_different_int(x):
#    x = set(x_)
    if len(x) < 2:  # Some models specified alldiff on 1 variable
        return x
    return (AllDiff([Variable(e,e,str(e)) if type(e) is int else e for e in x]))

def lex_less_int(x,y):
    if len(x) == 1 and len(y) == 1:
        return x[0] < y[0]
    return LessLex(x, y)

def lex_lesseq_int(x,y):
    if len(x) == 1 and len(y) == 1:
        return x[0] <= y[0]
    return LeqLex(x, y)

def lex_less_bool(x,y):
    return (lex_less_int(x,y))

def lex_lesseq_bool(x,y):
    return (lex_lesseq_int(x,y))

def minimum_int(x,y):
#    y = set(y_)
    if(len(y)==1):
        return (x == y[0])
    else:
        return (x == Min(y))

def maximum_int(x,y):
#    y = set(y_)
    if(len(y)==1):
        return (x == y[0])
    else:
        return (x == Max(y))

def table_int(x,t):
    return (Table([Variable(e,e,str(e)) if type(e) is int else e for e in x],[tuple([t[i * len(x) + j] for j in range(len(x))]) for i in range(len(t) / len(x))]))

def table_bool(x,t):
    return (table_int(x, t))


def total_seconds(td):
    return (td.microseconds + (td.seconds + td.days * 24 * 3600) * 1e6) / 1e6


def time_remaining(tcutoff):
    return max(tcutoff - total_seconds(datetime.datetime.now() - start_time), 0.0)


def run_solve(model, output_vars, param):
    load_time = datetime.datetime.now()
    encoding = NJEncodings[param['encoding']] if param['encoding'] else None
    solver = model.load(param['solver'], encoding=encoding)
    solver.setVerbosity(param['verbose'])
    time_limit = max(int(param['tcutoff'] - total_seconds(datetime.datetime.now() - load_time)), 1)
    solver.setTimeLimit(time_limit)
    solver.setHeuristic(param['var'], param['val'], param['rand'])
    solver.setThreadCount(param['threads'])
    if param['solver'] == 'Toulbar2':
        solver.setOption('lds',param['lds'])
        solver.setOption('lcLevel',param['lcLevel'])
        solver.setOption('deadEndElimination',param['dee'])
        solver.setOption('btdMode',param['btd'])
        solver.setOption('splitClusterMaxSize',param['rds'])
        solver.setOption('variableEliminationOrdering',param['varElimOrder'])
##        uncomment the following lines to save the problem in wcsp format
#        solver.setOption('nopre')
#        solver.setOption('lcLevel',0)
#        solver.setOption("dumpWCSP",2)
    if param['solver'] == 'Mistral':
        solver.solveAndRestart(param['restart'], param['base'], param['factor'])
    else:
        solver.solve()
    return solver, output_vars


def solve_main(param):
    model, output_vars = get_model()
    return run_solve(model, output_vars, param)

def get_model():
    model = Model()
    X_INTRODUCED_26 = [1,-1]
    X_INTRODUCED_108 = [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,-1]
    X_INTRODUCED_0 = Variable(0,8,'X_INTRODUCED_0')
    X_INTRODUCED_1 = Variable(0,8,'X_INTRODUCED_1')
    X_INTRODUCED_2 = Variable(0,8,'X_INTRODUCED_2')
    X_INTRODUCED_3 = Variable(0,8,'X_INTRODUCED_3')
    X_INTRODUCED_4 = Variable(0,8,'X_INTRODUCED_4')
    X_INTRODUCED_5 = Variable(0,8,'X_INTRODUCED_5')
    X_INTRODUCED_6 = Variable(0,8,'X_INTRODUCED_6')
    X_INTRODUCED_7 = Variable(0,8,'X_INTRODUCED_7')
    X_INTRODUCED_8 = Variable(0,8,'X_INTRODUCED_8')
    X_INTRODUCED_9 = Variable('X_INTRODUCED_9')
    X_INTRODUCED_10 = Variable('X_INTRODUCED_10')
    X_INTRODUCED_11 = Variable('X_INTRODUCED_11')
    X_INTRODUCED_12 = Variable('X_INTRODUCED_12')
    X_INTRODUCED_13 = Variable('X_INTRODUCED_13')
    X_INTRODUCED_14 = Variable('X_INTRODUCED_14')
    X_INTRODUCED_15 = Variable('X_INTRODUCED_15')
    X_INTRODUCED_16 = Variable('X_INTRODUCED_16')
    X_INTRODUCED_17 = Variable('X_INTRODUCED_17')
    X_INTRODUCED_18 = Variable('X_INTRODUCED_18')
    X_INTRODUCED_19 = Variable('X_INTRODUCED_19')
    X_INTRODUCED_20 = Variable('X_INTRODUCED_20')
    X_INTRODUCED_21 = Variable('X_INTRODUCED_21')
    X_INTRODUCED_22 = Variable('X_INTRODUCED_22')
    X_INTRODUCED_23 = Variable('X_INTRODUCED_23')
    X_INTRODUCED_24 = Variable('X_INTRODUCED_24')
    X_INTRODUCED_25 = Variable('X_INTRODUCED_25')
    satisfies = Variable(0,8,'satisfies')
    X_INTRODUCED_28 = Variable('X_INTRODUCED_28')
    X_INTRODUCED_30 = Variable('X_INTRODUCED_30')
    X_INTRODUCED_32 = Variable('X_INTRODUCED_32')
    X_INTRODUCED_34 = Variable('X_INTRODUCED_34')
    X_INTRODUCED_36 = Variable('X_INTRODUCED_36')
    X_INTRODUCED_38 = Variable('X_INTRODUCED_38')
    X_INTRODUCED_40 = Variable('X_INTRODUCED_40')
    X_INTRODUCED_42 = Variable('X_INTRODUCED_42')
    X_INTRODUCED_44 = Variable('X_INTRODUCED_44')
    X_INTRODUCED_46 = Variable('X_INTRODUCED_46')
    X_INTRODUCED_48 = Variable('X_INTRODUCED_48')
    X_INTRODUCED_50 = Variable('X_INTRODUCED_50')
    X_INTRODUCED_52 = Variable('X_INTRODUCED_52')
    X_INTRODUCED_54 = Variable('X_INTRODUCED_54')
    X_INTRODUCED_56 = Variable('X_INTRODUCED_56')
    X_INTRODUCED_58 = Variable('X_INTRODUCED_58')
    X_INTRODUCED_60 = Variable('X_INTRODUCED_60')
    X_INTRODUCED_62 = Variable('X_INTRODUCED_62')
    X_INTRODUCED_64 = Variable('X_INTRODUCED_64')
    X_INTRODUCED_66 = Variable('X_INTRODUCED_66')
    X_INTRODUCED_68 = Variable('X_INTRODUCED_68')
    X_INTRODUCED_70 = Variable('X_INTRODUCED_70')
    X_INTRODUCED_72 = Variable('X_INTRODUCED_72')
    X_INTRODUCED_74 = Variable('X_INTRODUCED_74')
    X_INTRODUCED_76 = Variable('X_INTRODUCED_76')
    X_INTRODUCED_78 = Variable('X_INTRODUCED_78')
    X_INTRODUCED_80 = Variable('X_INTRODUCED_80')
    X_INTRODUCED_82 = Variable('X_INTRODUCED_82')
    X_INTRODUCED_84 = Variable('X_INTRODUCED_84')
    X_INTRODUCED_86 = Variable('X_INTRODUCED_86')
    X_INTRODUCED_88 = Variable(0,1,'X_INTRODUCED_88')
    X_INTRODUCED_89 = Variable(0,1,'X_INTRODUCED_89')
    X_INTRODUCED_90 = Variable(0,1,'X_INTRODUCED_90')
    X_INTRODUCED_91 = Variable(0,1,'X_INTRODUCED_91')
    X_INTRODUCED_92 = Variable(0,1,'X_INTRODUCED_92')
    X_INTRODUCED_93 = Variable(0,1,'X_INTRODUCED_93')
    X_INTRODUCED_94 = Variable(0,1,'X_INTRODUCED_94')
    X_INTRODUCED_95 = Variable(0,1,'X_INTRODUCED_95')
    X_INTRODUCED_96 = Variable(0,1,'X_INTRODUCED_96')
    X_INTRODUCED_97 = Variable(0,1,'X_INTRODUCED_97')
    X_INTRODUCED_98 = Variable(0,1,'X_INTRODUCED_98')
    X_INTRODUCED_99 = Variable(0,1,'X_INTRODUCED_99')
    X_INTRODUCED_100 = Variable(0,1,'X_INTRODUCED_100')
    X_INTRODUCED_101 = Variable(0,1,'X_INTRODUCED_101')
    X_INTRODUCED_102 = Variable(0,1,'X_INTRODUCED_102')
    X_INTRODUCED_103 = Variable(0,1,'X_INTRODUCED_103')
    X_INTRODUCED_104 = Variable(0,1,'X_INTRODUCED_104')
#    pos = VarArray(9,-10000000,10000000,'pos')
#    model.add(pos[0] == [X_INTRODUCED_0,X_INTRODUCED_1,X_INTRODUCED_2,X_INTRODUCED_3,X_INTRODUCED_4,X_INTRODUCED_5,X_INTRODUCED_6,X_INTRODUCED_7,X_INTRODUCED_8][0])
#    model.add(pos[1] == [X_INTRODUCED_0,X_INTRODUCED_1,X_INTRODUCED_2,X_INTRODUCED_3,X_INTRODUCED_4,X_INTRODUCED_5,X_INTRODUCED_6,X_INTRODUCED_7,X_INTRODUCED_8][1])
#    model.add(pos[2] == [X_INTRODUCED_0,X_INTRODUCED_1,X_INTRODUCED_2,X_INTRODUCED_3,X_INTRODUCED_4,X_INTRODUCED_5,X_INTRODUCED_6,X_INTRODUCED_7,X_INTRODUCED_8][2])
#    model.add(pos[3] == [X_INTRODUCED_0,X_INTRODUCED_1,X_INTRODUCED_2,X_INTRODUCED_3,X_INTRODUCED_4,X_INTRODUCED_5,X_INTRODUCED_6,X_INTRODUCED_7,X_INTRODUCED_8][3])
#    model.add(pos[4] == [X_INTRODUCED_0,X_INTRODUCED_1,X_INTRODUCED_2,X_INTRODUCED_3,X_INTRODUCED_4,X_INTRODUCED_5,X_INTRODUCED_6,X_INTRODUCED_7,X_INTRODUCED_8][4])
#    model.add(pos[5] == [X_INTRODUCED_0,X_INTRODUCED_1,X_INTRODUCED_2,X_INTRODUCED_3,X_INTRODUCED_4,X_INTRODUCED_5,X_INTRODUCED_6,X_INTRODUCED_7,X_INTRODUCED_8][5])
#    model.add(pos[6] == [X_INTRODUCED_0,X_INTRODUCED_1,X_INTRODUCED_2,X_INTRODUCED_3,X_INTRODUCED_4,X_INTRODUCED_5,X_INTRODUCED_6,X_INTRODUCED_7,X_INTRODUCED_8][6])
#    model.add(pos[7] == [X_INTRODUCED_0,X_INTRODUCED_1,X_INTRODUCED_2,X_INTRODUCED_3,X_INTRODUCED_4,X_INTRODUCED_5,X_INTRODUCED_6,X_INTRODUCED_7,X_INTRODUCED_8][7])
#    model.add(pos[8] == [X_INTRODUCED_0,X_INTRODUCED_1,X_INTRODUCED_2,X_INTRODUCED_3,X_INTRODUCED_4,X_INTRODUCED_5,X_INTRODUCED_6,X_INTRODUCED_7,X_INTRODUCED_8][8])
    pos = VarArray([X_INTRODUCED_0,X_INTRODUCED_1,X_INTRODUCED_2,X_INTRODUCED_3,X_INTRODUCED_4,X_INTRODUCED_5,X_INTRODUCED_6,X_INTRODUCED_7,X_INTRODUCED_8])
#    X_INTRODUCED_107 = VarArray(18,-10000000,10000000,'X_INTRODUCED_107')
#    model.add(X_INTRODUCED_107[0] == [X_INTRODUCED_88,X_INTRODUCED_89,X_INTRODUCED_90,X_INTRODUCED_91,X_INTRODUCED_92,X_INTRODUCED_93,X_INTRODUCED_94,X_INTRODUCED_95,X_INTRODUCED_96,X_INTRODUCED_97,X_INTRODUCED_98,X_INTRODUCED_99,X_INTRODUCED_100,X_INTRODUCED_101,X_INTRODUCED_102,X_INTRODUCED_103,X_INTRODUCED_104,satisfies][0])
#    model.add(X_INTRODUCED_107[1] == [X_INTRODUCED_88,X_INTRODUCED_89,X_INTRODUCED_90,X_INTRODUCED_91,X_INTRODUCED_92,X_INTRODUCED_93,X_INTRODUCED_94,X_INTRODUCED_95,X_INTRODUCED_96,X_INTRODUCED_97,X_INTRODUCED_98,X_INTRODUCED_99,X_INTRODUCED_100,X_INTRODUCED_101,X_INTRODUCED_102,X_INTRODUCED_103,X_INTRODUCED_104,satisfies][1])
    X_INTRODUCED_107 = VarArray([X_INTRODUCED_88,X_INTRODUCED_89,X_INTRODUCED_90,X_INTRODUCED_91,X_INTRODUCED_92,X_INTRODUCED_93,X_INTRODUCED_94,X_INTRODUCED_95,X_INTRODUCED_96,X_INTRODUCED_97,X_INTRODUCED_98,X_INTRODUCED_99,X_INTRODUCED_100,X_INTRODUCED_101,X_INTRODUCED_102,X_INTRODUCED_103,X_INTRODUCED_104,satisfies])
    model.add( int_lin_eq(X_INTRODUCED_108,X_INTRODUCED_107,0) )
    model.add( all_different_int(pos) )
    model.add( int_lin_le(X_INTRODUCED_26,[X_INTRODUCED_0,X_INTRODUCED_1],-1) )
    model.add( bool_xor(X_INTRODUCED_28,X_INTRODUCED_30,X_INTRODUCED_9) )
    model.add( bool_xor(X_INTRODUCED_32,X_INTRODUCED_34,X_INTRODUCED_10) )
    model.add( bool_xor(X_INTRODUCED_36,X_INTRODUCED_38,X_INTRODUCED_11) )
    model.add( bool_xor(X_INTRODUCED_40,X_INTRODUCED_42,X_INTRODUCED_12) )
    model.add( bool_xor(X_INTRODUCED_44,X_INTRODUCED_46,X_INTRODUCED_13) )
    model.add( bool_xor(X_INTRODUCED_48,X_INTRODUCED_50,X_INTRODUCED_14) )
    model.add( bool_xor(X_INTRODUCED_52,X_INTRODUCED_54,X_INTRODUCED_15) )
    model.add( bool_xor(X_INTRODUCED_56,X_INTRODUCED_58,X_INTRODUCED_16) )
    model.add( bool_xor(X_INTRODUCED_60,X_INTRODUCED_62,X_INTRODUCED_17) )
    model.add( bool_xor(X_INTRODUCED_64,X_INTRODUCED_66,X_INTRODUCED_18) )
    model.add( bool_xor(X_INTRODUCED_34,X_INTRODUCED_32,X_INTRODUCED_19) )
    model.add( bool_xor(X_INTRODUCED_68,X_INTRODUCED_70,X_INTRODUCED_20) )
    model.add( bool_xor(X_INTRODUCED_72,X_INTRODUCED_74,X_INTRODUCED_21) )
    model.add( bool_xor(X_INTRODUCED_76,X_INTRODUCED_78,X_INTRODUCED_22) )
    model.add( bool_xor(X_INTRODUCED_80,X_INTRODUCED_82,X_INTRODUCED_23) )
    model.add( bool_xor(X_INTRODUCED_84,X_INTRODUCED_86,X_INTRODUCED_24) )
    model.add( bool_xor(X_INTRODUCED_82,X_INTRODUCED_80,X_INTRODUCED_25) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_2,X_INTRODUCED_0],1,X_INTRODUCED_28) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_0,X_INTRODUCED_2],1,X_INTRODUCED_30) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_4,X_INTRODUCED_0],1,X_INTRODUCED_32) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_0,X_INTRODUCED_4],1,X_INTRODUCED_34) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_7,X_INTRODUCED_0],1,X_INTRODUCED_36) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_0,X_INTRODUCED_7],1,X_INTRODUCED_38) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_4,X_INTRODUCED_1],1,X_INTRODUCED_40) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_1,X_INTRODUCED_4],1,X_INTRODUCED_42) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_8,X_INTRODUCED_1],1,X_INTRODUCED_44) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_1,X_INTRODUCED_8],1,X_INTRODUCED_46) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_3,X_INTRODUCED_2],1,X_INTRODUCED_48) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_2,X_INTRODUCED_3],1,X_INTRODUCED_50) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_4,X_INTRODUCED_2],1,X_INTRODUCED_52) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_2,X_INTRODUCED_4],1,X_INTRODUCED_54) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_0,X_INTRODUCED_3],1,X_INTRODUCED_56) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_3,X_INTRODUCED_0],1,X_INTRODUCED_58) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_4,X_INTRODUCED_3],1,X_INTRODUCED_60) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_3,X_INTRODUCED_4],1,X_INTRODUCED_62) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_5,X_INTRODUCED_4],1,X_INTRODUCED_64) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_4,X_INTRODUCED_5],1,X_INTRODUCED_66) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_0,X_INTRODUCED_5],1,X_INTRODUCED_68) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_5,X_INTRODUCED_0],1,X_INTRODUCED_70) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_8,X_INTRODUCED_5],1,X_INTRODUCED_72) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_5,X_INTRODUCED_8],1,X_INTRODUCED_74) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_2,X_INTRODUCED_6],1,X_INTRODUCED_76) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_6,X_INTRODUCED_2],1,X_INTRODUCED_78) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_7,X_INTRODUCED_6],1,X_INTRODUCED_80) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_6,X_INTRODUCED_7],1,X_INTRODUCED_82) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_8,X_INTRODUCED_7],1,X_INTRODUCED_84) )
    model.add( int_lin_eq_reif(X_INTRODUCED_26,[X_INTRODUCED_7,X_INTRODUCED_8],1,X_INTRODUCED_86) )
    model.add( bool2int(X_INTRODUCED_9,X_INTRODUCED_88) )
    model.add( bool2int(X_INTRODUCED_10,X_INTRODUCED_89) )
    model.add( bool2int(X_INTRODUCED_11,X_INTRODUCED_90) )
    model.add( bool2int(X_INTRODUCED_12,X_INTRODUCED_91) )
    model.add( bool2int(X_INTRODUCED_13,X_INTRODUCED_92) )
    model.add( bool2int(X_INTRODUCED_14,X_INTRODUCED_93) )
    model.add( bool2int(X_INTRODUCED_15,X_INTRODUCED_94) )
    model.add( bool2int(X_INTRODUCED_16,X_INTRODUCED_95) )
    model.add( bool2int(X_INTRODUCED_17,X_INTRODUCED_96) )
    model.add( bool2int(X_INTRODUCED_18,X_INTRODUCED_97) )
    model.add( bool2int(X_INTRODUCED_19,X_INTRODUCED_98) )
    model.add( bool2int(X_INTRODUCED_20,X_INTRODUCED_99) )
    model.add( bool2int(X_INTRODUCED_21,X_INTRODUCED_100) )
    model.add( bool2int(X_INTRODUCED_22,X_INTRODUCED_101) )
    model.add( bool2int(X_INTRODUCED_23,X_INTRODUCED_102) )
    model.add( bool2int(X_INTRODUCED_24,X_INTRODUCED_103) )
    model.add( bool2int(X_INTRODUCED_25,X_INTRODUCED_104) )
    model.add(Maximize(satisfies))
    output_vars = (pos, satisfies)
    return model, output_vars


def solve_dichotomic(param):
    model, output_vars = get_model()
    pos, satisfies = output_vars
    lb = reallb = satisfies.lb
    ub = realub = satisfies.ub
    best_sol = (None, output_vars)
    dichotomic_sat = dichotomic_opt = False
    while lb + 1 < ub and time_remaining(param['tcutoff']) > param['dichtcutoff']:
        newobj = (lb + ub) / 2
        # print lb, ub, newobj
        dummymodel, output_vars = get_model()
        pos, satisfies = output_vars
        dummymodel.add(satisfies <= realub)
        dummymodel.add(satisfies > newobj)
        dichparam = dict(param)
        dichparam['tcutoff'] = param['dichtcutoff']
        solver, output_vars = run_solve(dummymodel, output_vars, dichparam)

        if solver.is_opt():
            ub = realub = satisfies.get_value() + 1
        if solver.is_sat():
            lb = satisfies.get_value()
            best_sol = solver, output_vars
            dichotomic_sat = True
        elif solver.is_unsat():
            ub = realub = newobj
        else:
            ub = newobj

    if realub > lb + 1:
        dummymodel, output_vars = get_model()
        pos, satisfies = output_vars
        dummymodel.add(satisfies <= realub)
        dummymodel.add(satisfies > lb)
        tcutoff = time_remaining(param['tcutoff'])
        if tcutoff > 1.0:
            dichparam = dict(param)
            dichparam['tcutoff'] = tcutoff
            solver, output_vars = run_solve(dummymodel, output_vars, dichparam)
            if solver.is_sat():
                best_sol = solver, output_vars
    else:
        dichotomic_opt = True

    if not solver.is_sat() and dichotomic_sat:
        best_sol[0].is_sat = lambda: True
        best_sol[0].is_unsat = lambda: False
        if dichotomic_opt:
            best_sol[0].is_opt = lambda: True
    return best_sol


start_time = datetime.datetime.now()


if __name__ == '__main__':
    solvers = ['Mistral', 'SCIP', 'MiniSat', 'Toulbar2', 'Gurobi']
    default = dict([('solver', 'Toulbar2'), ('verbose', 0), ('tcutoff', 900), ('var', 'DomainOverWDegree'), ('val', 'Lex'), ('rand', 2), ('threads', 1), ('restart', GEOMETRIC), ('base', 256), ('factor', 1.3), ('lcLevel', 4), ('lds', 0), ('dee',0), ('btd',0), ('rds',0), ('varElimOrder', 0), ('dichotomic', 0), ('dichtcutoff', 5), ('encoding', '')])
    param = input(default)
    if param['dichotomic'] == 1:
        solver, output_vars = solve_dichotomic(param)
    else:
        solver, output_vars = solve_main(param)
    pos, satisfies = output_vars

    if not solver:
        print '=====UNKNOWN====='
        sys.exit(0)

    if solver.is_sat():
        print 'pos = array1d(0..8,',str(pos),');'
        print 'satisfies = ', (solver.getOptimum() if param['solver'] == 'Toulbar2' else satisfies.get_value()),';'
        print '----------'
        if solver.is_opt():
            print '=========='
    elif solver.is_unsat():
        print '=====UNSATISFIABLE====='
    else:
        print '=====UNKNOWN====='
    print '% SolveTime', solver.getTime()
    print '% Nodes', solver.getNodes()
    print '% Failures', solver.getFailures()
    if solver.is_sat(): print '% Objective', (solver.getOptimum() if param['solver'] == 'Toulbar2' else satisfies.get_value())
