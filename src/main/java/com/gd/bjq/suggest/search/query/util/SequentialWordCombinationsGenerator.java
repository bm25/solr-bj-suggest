package com.gd.bjq.suggest.search.query.util;

import java.util.*;
import java.util.stream.Collectors;

public class SequentialWordCombinationsGenerator {
    private List<String> words;

    public SequentialWordCombinationsGenerator(List<String> words) {
        this.words = words;
    }

    public List<List<String>> generate(){
        return toList(generateTreeOfCombinations());
    }

    private Deque<Node> generateTreeOfCombinations(){
        if (words == null || words.size() == 0){
            throw new IllegalArgumentException("The list of words is empty");
        }

        Deque<Node> queue = new LinkedList<>();
        int wordsCount = words.size();
        String root = words.get(0);
        Node node = new Node(new LinkedList<>(Arrays.asList(root)), 0);
        queue.offerLast(node);
        int level = 0;

        while ((level < wordsCount) && (queue.size() < (1 << (wordsCount-1)))){
            node = queue.pollFirst();
            level = node.getLevel() + 1;

            String nextWord = words.get(level);
            Deque<String> combinationRight = createListConcatingWordToLastElem(node.getCombinationOfWords(), nextWord);
            Deque<String> combinationLeft = new LinkedList<>(node.getCombinationOfWords());
            combinationLeft.offerLast(nextWord);

            queue.offerLast(new Node(combinationLeft,level));
            queue.offerLast(new Node(combinationRight,level));
        }

        return queue;
    }

    private Deque<String> createListConcatingWordToLastElem(Queue<String> oldList, String newWord){
        Deque<String> result = new LinkedList<>(oldList);
        String lastWord = result.pollLast();
        lastWord = new StringBuilder(lastWord).append(" ").append(newWord).toString();
        result.offerLast(lastWord);
        return result;
    }

    private List<List<String>> toList(Queue<Node> queue){
        return queue.stream()
                .map(node -> node.getCombinationOfWords()
                        .stream()
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public class Node {
        private Deque<String> combinationOfWords;
        private int level;

        public Node(Deque<String> combinationOfWords, int level) {
            this.combinationOfWords = combinationOfWords;
            this.level = level;
        }

        public Deque<String> getCombinationOfWords() {
            return combinationOfWords;
        }

        public int getLevel() {
            return level;
        }
    }
}
