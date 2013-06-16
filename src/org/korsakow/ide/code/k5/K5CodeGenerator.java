/**
 * 
 */
package org.korsakow.ide.code.k5;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.korsakow.domain.Keyword;
import org.korsakow.domain.interf.IRule;
import org.korsakow.ide.rules.RuleType;

/**
 * Class for creating K5 source code, with helper methods for converting from K5.
 * @author d
 *
 */
public class K5CodeGenerator
{
	/**
	 * Any K5 rules that have no equivalent in code are simply omitted.
	 */
	public K5Code createK5CodeOmitUnsupported(List<IRule> rules, boolean isFirstRuleSet)
	{
		rules = new ArrayList<IRule>(rules); // we're going to modify it
		
		StringBuilder sb = new StringBuilder();
				
		for (IRule rule : rules) {
			try {
				sb.append(createK5Code(rule).getRawCode())
					.append(K5Symbol.DEFAULT_STATEMENT_SEPARATOR)
				.append(" ");
			} catch (NoK5CodeEquivalentException e) {
				// just omit these rules as intended
				// we might want to provide some feedback on which ones were omitted though
			}
		}
		
		removeTrailingWhitespace(sb);
		K5Code code = new K5Code(sb.toString());
		code.setValid(true); // since each one was valid individually
		return code;
	}
	public K5Code createK5Code(IRule rule) throws NoK5CodeEquivalentException
	{
		K5Code code;
		RuleType type = null;
		try {
			type = RuleType.forId(rule.getRuleType());
		} catch (IllegalArgumentException e) {
			throw new NoK5CodeEquivalentException("no such rule: " + rule.getRuleType());
		}
		switch (type)
		{
		case KeywordLookup:
			code = new K5Code(createKeywordLookup(Keyword.toStrings(rule.getKeywords())));
			break;
		case RequireKeywords:
			code = new K5Code(createRequireKeyword(Keyword.toStrings(rule.getKeywords())));
			break;
		case ExcludeKeywords:
			code = new K5Code(createExcludeKeyword(Keyword.toStrings(rule.getKeywords())));
			break;
		case RandomLookup:
			code = new K5Code(createRandomLookup());
			break;
		case EndfilmLookup:
			code = new K5Code(createOutboundEndfilm());
			break;
		case RequireEndfilm:
			code = new K5Code(createRequireEndfilm());
			break;
		case ExcludeEndfilm:
			code = new K5Code(createExcludeEndfilm());
			break;
		case SetEndfilm:
			code = new K5Code(createSetEndfilm());
			break;
		case ClearScores:
			code = new K5Code(createClearPreviousLinks());
			break;
		default:
			throw new NoK5CodeEquivalentException("no k5 equivalent for: " + type.name());
		}
		code.setValid(true); // by definition, we only generate valid code
		return code;
	}
	public String createKeywordLookup(Collection<String> keywords)
	{
		StringBuilder sb = new StringBuilder();
		for (String keyword : keywords)
			sb.append(keyword)
				.append(K5Symbol.DEFAULT_STATEMENT_SEPARATOR)
				.append(" ");
		removeTrailingWhitespace(sb);
		return sb.toString();
	}
	public String createRandomLookup()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(K5Symbol.RANDOM_KEYWORD);
		removeTrailingWhitespace(sb);
		return sb.toString();
	}
	public String createOutboundEndfilm()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(K5Symbol.ENDFILM_KEYWORD);
		removeTrailingWhitespace(sb);
		return sb.toString();
	}
	public String createRequireEndfilm()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(K5Symbol.ENDFILM_KEYWORD)
			.append(K5Symbol.REQUIRED_KEYWORD);
		removeTrailingWhitespace(sb);
		return sb.toString();
	}
	public String createExcludeEndfilm()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(K5Symbol.ENDFILM_KEYWORD)
			.append(K5Symbol.EXCLUSION_KEYWORD);
		removeTrailingWhitespace(sb);
		return sb.toString();
	}
	public String createSetEndfilm()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(K5Symbol.ENDFILM_KEYWORD);
		removeTrailingWhitespace(sb);
		return sb.toString();
	}
	public String createRequireKeyword(Collection<String> keywords)
	{
		StringBuilder sb = new StringBuilder();
		for (String keyword : keywords)
			sb.append(keyword)
				.append(K5Symbol.REQUIRED_KEYWORD)
				.append(K5Symbol.DEFAULT_STATEMENT_SEPARATOR)
				.append(" ");
		removeTrailingWhitespace(sb);
		return sb.toString();
	}
	public String createExcludeKeyword(Collection<String> keywords)
	{
		StringBuilder sb = new StringBuilder();
		for (String keyword : keywords)
			sb.append(keyword)
				.append(K5Symbol.EXCLUSION_KEYWORD)
				.append(K5Symbol.DEFAULT_STATEMENT_SEPARATOR)
				.append(" ");
		removeTrailingWhitespace(sb);
		return sb.toString();
	}
	public String createClearPreviousLinks()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(K5Symbol.CLEAR_PREVIOUS_LINKS);
		removeTrailingWhitespace(sb);
		return sb.toString();
	}
	public String createKeepPreviousLinks()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(K5Symbol.KEEP_PREVIOUS_LINKS);
		removeTrailingWhitespace(sb);
		return sb.toString();
	}
	private void removeTrailingWhitespace(StringBuilder sb)
	{
		while (sb.length()>0 && (sb.charAt(0) == ' ' || sb.charAt(0) == '\t')) // trim left
			sb.deleteCharAt(0);
		while (sb.length()>0 && (sb.charAt(sb.length()-1) == ' ' || sb.charAt(sb.length()-1) == '\t')) // trim right
			sb.deleteCharAt(sb.length()-1);
		if (sb.length() != 0 &&  sb.lastIndexOf(K5Symbol.DEFAULT_STATEMENT_SEPARATOR_STRING) == (sb.length()-K5Symbol.DEFAULT_STATEMENT_SEPARATOR_STRING.length()))
			sb.delete(sb.length()-K5Symbol.DEFAULT_STATEMENT_SEPARATOR_STRING.length(), sb.length());
	}
}