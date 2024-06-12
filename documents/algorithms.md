# Algorithms

## Local flat foldability

Local flat foldability means whether area around a vertex is foldable or not.
Note that combining Kawasaki's theorem, Maekawa's theorem and big-little-big lemma
is not sufficient to check the foldability. (If we also apply "generalized" big-little-big lemma,
then it is sufficient.)
ORIPA implements the linear time algorithm by E. Demaine and J. O'Rourke
described in their book "Geometric Folding Algorithms: Linkages, Origami, Polyhedra",
Cambridge University Press.

## Line suggestion

See this [pdf](https://github.com/oripa/oripa/files/9242593/flatten_by_kawasaki_theorem.pdf)
for the problem definition and the proposed algorithm.
After applying the algorithm, we filter the lines by the local flat foldability test.

## Fold algorithm

Japanese text is written by J. Mitani as a part of https://mitani.cs.tsukuba.ac.jp/origami/.
Here we don't translate it but summarizes the essence of the algorithm.
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
The necessity of mirroring can be computed by the fact that a fold along a crease turns over the neighbor face
regardless of the mountain/valley of the crease.

### Step 3: determine overlap relations between faces while detecting penetrations and contradictions of the relations.

This is deterministic, that is, we don't have to do trial and error.
For example, assume a face $A$ is split by a crease and the crease is shared with two other faces
$B$ and $C$. $A$ penetrates the sheet if $A$ is between $B$ and $C$, otherwise we can determine 
$A$ is above/under $B$ and $C$.
ORIPA tests such conditions (there are some other conditions) repeatedly 
until no change happens to the overlap relation matrix that stores "above"/"under" of each pair of the faces.

### Step 4: enumerate all foldable overlap relation matrices by backtracking.

After step 2, there are smaller faces surrounded by the edges. We call them subfaces.
Each face consists of one or more subfaces and a subface is an area shared with the moved faces.
Strictly speaking, a subface is a maximal area that does not include any part of the edges.
Here we consider "subface stack", which is a list of faces that share a subface. 
We generate a list $S$ of stacks for each subface and 
check that there is no contradiction on the overlap relation matrix
if a stack $s \in S$ is applied.
There can be many, really many, patterns for a stack of certain subface. ORIPA reduces the patterns with
some conditions on stacking: for example, if a face $A$ is put to the list and it has a neighbor face $B$
that should be under $A$, then we can stop pattern generation when $B$ gets above $A$.
The algorithm outputs the overlap relation matrix when the matrix is fulfilled.
