package com.ocado.basket;

import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;

public class BasketSplitter {
    private final Map<String, Integer> suppliers = new HashMap<>();
    private final Map<String, ArrayList<Integer>> products = new HashMap<>();

    public BasketSplitter(String absolutePathToConfigFile) {

        try {
            File file = new File(absolutePathToConfigFile);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(file);

            Iterator<String> fieldNames = jsonNode.fieldNames();
            while (fieldNames.hasNext()) {
                String productName = fieldNames.next();
                JsonNode listOfSuppliers = jsonNode.get(productName);
                ArrayList<Integer> currentSuppliers = new ArrayList<>();
                for (JsonNode node : listOfSuppliers) {
                    if (!suppliers.containsKey(node.asText())) {
                        suppliers.put(node.asText(), suppliers.size());
                    }
                    currentSuppliers.add(suppliers.get(node.asText()));
                }

                products.put(productName, currentSuppliers);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int findBestPrevious(int[][][] table, int wiersz, List<Integer> possibleColumns) {
        int column = -1;
        int mini = suppliers.size() + 1;

        for (int currentColumn : possibleColumns) {
            int currVal = 0;
            for (int i = 0; i < suppliers.size(); i++) {
                if (table[wiersz][currentColumn][i] != 0) {
                    currVal += 1;
                }
            }
            if (currVal < mini) {
                mini = currVal;
                column = currentColumn;
            }
        }

        return column;
    }

    public ArrayList<Integer> optimalSuppliers(int[][] table, String lastProduct) {
        int mini = suppliers.size();
        int bestColumn = -1;
        for (int possibility : products.get(lastProduct)) {
            int current = 0;
            for (int i = 0; i < suppliers.size(); i++) {
                if (table[possibility][i] != 0) {
                    current += 1;
                }
            }
            if (current < mini) {
                mini = current;
                bestColumn = possibility;
            }
        }

        ArrayList<Integer> bestOutcome = new ArrayList<>();

        for (int i = 0; i < suppliers.size(); i++) {
            if (table[bestColumn][i] != 0) {
                bestOutcome.add(i);
            }
        }
        return bestOutcome;
    }

    // Puts items into given Suppliers in best possible way.
    public Map<String, List<String>> bestArrangement(List<String> items, List<Integer> bestSuppliers) {

        int[] table = new int[suppliers.size()];
        Arrays.fill(table, 0);
        int n = bestSuppliers.size();
        Map<String, List<String>> answer = new HashMap<>();
        for (int i = 0; i < n; i++) {
            for (String item : items) {
                for (int index : products.get(item)) {
                    table[index] += 1;
                }
            }
            int maksi = 0;
            Integer bestIndex = -1;
            for (int index : bestSuppliers) {
                if (table[index] > maksi) {
                    maksi = table[index];
                    bestIndex = index;
                }
            }
            Arrays.fill(table, 0);
            bestSuppliers.remove(bestIndex);
            List<String> currentProducts = new ArrayList<>();

            List<String> copy = new ArrayList<>(items);
            for (String item : copy) {
                if (products.get(item).contains(bestIndex)) {
                    currentProducts.add(item);
                    items.remove(item);
                }
            }
            Integer finalBestIndex = bestIndex;
            answer.put(suppliers.entrySet().stream().
                    filter(entry -> finalBestIndex.equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null), currentProducts);


        }
        answer.remove(null);

        return answer;

    }


    public Map<String, List<String>> split(List<String> items) {
        int[][][] dynamicStructure = new int[items.size()][suppliers.size()][suppliers.size()];
        String startingItem = items.get(0);

        for (Integer possibleSupplier : products.get(startingItem)) {
            dynamicStructure[0][possibleSupplier][possibleSupplier] = 1;
        }

        for (int i = 1; i < items.size(); i++) {
            String currentItem = items.get(i);

            for (int column : products.get(currentItem)) {
                int copyColumn = findBestPrevious(dynamicStructure, i - 1, products.get(startingItem));

                for (int j = 0; j < suppliers.size(); j++) {

                    dynamicStructure[i][column][j] = dynamicStructure[i - 1][copyColumn][j];
                    dynamicStructure[i][column][column] += 1;
                }


            }
            startingItem = currentItem;
        }

        ArrayList<Integer> bestSuppliers = optimalSuppliers(dynamicStructure[items.size() - 1], items.get(items.size() - 1));

        return bestArrangement(items, bestSuppliers);
    }
}

