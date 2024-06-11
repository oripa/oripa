# Algorithms

## Local flat foldability

Local flat foldability means whether area around a vertex is foldable or not.
Note that combining Kawasaki's theorem, Maekawa's thorem and big-little-big lemma
is not sufficient to check the foldability.
ORIPA implements the linear time algorithm by E. Demaine and J. O'Rourke
described in their book "Geometric Folding Algorithms: Linkages, Origami, Polyhedra",
Cambridge University Press.

## Line suggestion

See https://github.com/oripa/oripa/issues/203 for the problem definition and the proposed algorithm.

## Fold algorithm

Japanese text is written by J. Mitani at https://mitani.cs.tsukuba.ac.jp/origami/.
The implementation leverages parallel computation heavily to reduce computation time.
ORIPA finds all possible folded states while [Oriedita](https://github.com/oriedita/oriedita) doesn't for some cases.

### Step 1: check local flat foldability for each vertex.

Run the algorithm for the test for each vertex.

### Step 2: cut the sheet of paper along the creases and move all faces according to creases. 

The move is done by composition of mirror transforms whose axes are the creases.
ORIPA implements this transform without Affine transform but does with the aspect of geometry:
If a face $A$ has been transformed, then face $B$ that was a neighbor of $A$ before the transform
should be still connected on the edge that was shared with them after fold.
So first we mirror $B$ with the axis of the shared crease if necessary,
then move $B$ as it will be connected on the crease of the transformed $A$.

The necessity of mirroring can be computed by the fact that a fold along a crease reverses the neighbor face
regardless of the mountain/valley of the crease.

### Step 3: determine overlap relations between faces while detecting penetrations and contradictions of the relations.

This is deterministic, that is, we don't have to do trial and error.
For example, assume a face $A$ is split by a crease and the crease is shared with two other faces
$B$ and $C$. $A$ penetrates the sheet if $A$ is between $B$ and $C$, otherwise we can determine 
$A$ is above/under $B$ and $C$.

ORIPA tests such conditions (there are some other conditions) repeatedly 
until no change happens to the overlap relation matrix.

### Step 4: enumerate all possible overlap relation matrices by backtracking.

After step 2, there are smaller faces surrounded by the edges. We call them subfaces.
Each face consists of one or more subfaces and a subface is a shared area of the moved faces.
Here we consider "subface stack", which is a list of faces that share a subface. 
There can be many ,really many, patterns for a stack. ORIPA reduces the patterns by
some conditions on stacking, For example, if face $A$ is put to the list and it has a neighbor $B$
that should be under $A$, then we can stop pattern generation when $B$ gets above $A$.

We generate a list $S$ of stacks for each subface and 
check that there is no contraction on the overlap relation matrix
if a stack $s \in S$ is applied.
The algorithm outputs the overlap relation matrix when the matrix is fulfilled.
