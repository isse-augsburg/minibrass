import sqlite3
import numpy as np
import scipy.stats as st

class GeomMean:
  def __init__(self):
      self.values = []
      
  def step(self, value):
      self.values += [value]
    
  def finalize(self):
      return st.gmean(self.values)

conn = sqlite3.connect(":memory:")

conn.create_aggregate("GeomMean", 1, GeomMean)

cur = conn.cursor()
cur.execute("create table test(i)")
cur.execute("insert into test(i) values (1)")
cur.execute("insert into test(i) values (2)")

cur.execute("select GeomMean(i) from test")
print cur.fetchone()[0]
