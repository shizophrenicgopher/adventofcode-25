void main() throws IOException {
    var lines = Files.lines(Path.of("input06.txt")).map(line -> line.trim().split(" +")).toList();
    var lines2 = new ArrayList<>(Files.lines(Path.of("input06.txt")).toList());

    IO.println("Part 1: " + part1(lines));
    IO.println("Part 2: " + part2(lines2));
}

private long part1(List<String[]> lines) {
    long sum = 0;
    for (int i = 0; i < lines.getFirst().length; i++) {
        int finalI = i;
        var numbers = lines.stream()
                .filter(line -> !line[0].equals("*") && !line[0].equals("+"))
                .mapToLong(line -> Long.parseLong(line[finalI]));

        var operator = lines.getLast()[i];
        if (operator.equals("+")) {
            sum += numbers.sum();
        } else {
            sum += numbers.reduce(1, (acc, element) -> acc * element);
        }
    }
    return sum;
}

private long part2(List<String> lines) {
    var operators = lines.removeLast().toCharArray();
    var numbers = new ArrayList<Long>();
    var sum = 0L;

    for (int i = operators.length - 1; i >= 0; i--) {
        int finalI = i;
        numbers.add(Long.parseLong(lines.stream().map(line -> String.valueOf(line.toCharArray()[finalI])).collect(Collectors.joining()).trim()));

        if (operators[i] == '+') {
            sum += numbers.stream().reduce(0L, Long::sum);
            numbers.clear();
            i--;
        } else if (operators[i] == '*') {
            sum += numbers.stream().reduce(1L, (acc, element) -> acc * element);
            numbers.clear();
            i--;
        }
    }

    return sum;
}
