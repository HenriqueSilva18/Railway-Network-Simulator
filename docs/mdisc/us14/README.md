Algorithm Fleury(Graph G)
Input: A connected graph G
Output: An Eulerian path/trail or an Eulerian circuit if it exists, otherwise a message

1. Count the degree of all vertices in G
2. Check if G is Eulerian or semi-Eulerian:
   a. If there are more than 2 odd-degree vertices, return "No Eulerian path exists"
   b. If there are 2 odd-degree vertices, G is semi-Eulerian (has Eulerian path)
   c. If all vertices have even degree, G is Eulerian (has Eulerian circuit)

3. Choose a starting vertex:
   a. If G is semi-Eulerian, choose one of the odd-degree vertices
   b. If G is Eulerian, choose any vertex

4. Initialize an empty path P

5. While the current vertex has unvisited edges:
   a. For each edge (current, neighbor) from current vertex:
   i. If removing edge (current, neighbor) doesn't disconnect the graph, or it's the only remaining edge:
   - Add edge to path P
   - Remove edge from G
   - Set current = neighbor
   - Break the inner loop

6. Return path P