package com.Contracts;

import java.util.List;

public interface IPreprocessingUtility
{
    List<String> removeStopwords(List<String> input);
    List<String> removeAbbreviations(List<String> input);
    List<String> ensureTokenMinLength(List<String> input, int tokenMinLength);
    String fixWhiteSpaces(String input);
    String normalizeWhitespaces(String input);
    String transformToLowerCaseTrim(String input);
    String replaceUmlauts(String input);
    String replaceDates(String input);
    String replaceSpecialCharacters(String input);
    String normalizeText(String input);
    String unescapeText(String input);
    String replaceNonAsciiCharacters(String input);
    String replaceAllSentenceEndingWithDot(String input);
    String replaceHyphenAndApostropheWithWhitespace(String input);
    String replaceSpecialCharactersExceptDot(String input);
    String replaceSpecialMedCharacters(String input);
    String repairMissingWhitespaces(String input);
}