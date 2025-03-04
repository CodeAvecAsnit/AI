import math



def distance(p1,p2):
  """Euclidean distance betweeen two points."""
  return math.sqrt(math.sum(p1-p2)**2)