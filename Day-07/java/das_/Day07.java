void main() throws IOException {
    var grid = Files.lines(Path.of("input07.txt"))
            .map(line -> line.split(""))
            .toArray(String[][]::new);

    IO.println("Part 1: " + part1(grid));
    IO.println("Part 2: " + part2(grid));
}

private int part1(String[][] grid) {
    int splits = 0;
    var beans = ConcurrentHashMap.<Integer>newKeySet();
    beans.add(70);

    for (int k = 2; k < grid.length; k += 2) {
        for (int bean : beans) {
            if (grid[k][bean].equals("^")) {
                beans.addAll(Set.of(bean - 1, bean + 1));
                beans.remove(bean);
                splits++;
            }
        }
    }

    return splits;
}

long part2(String[][] grid) {
    var columns = new long[grid[0].length];
    Arrays.fill(columns, 1L);

    for (int k = grid.length - 2; k > 1; k -= 2) {
        for (int i = 0; i < grid[0].length; i++) {
            columns[i] = grid[k][i].equals("^") ? columns[i - 1] + columns[i + 1] : columns[i];
        }
    }

    return columns[70];
}
