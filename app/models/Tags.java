package models;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.TermEnum;

import play.Logger;
import play.cache.Cache;
import play.modules.search.Search;

public class Tags {

    public static String CACHE_KEY = "alltags";

    public static Set<String> allTags() throws IOException {
        Set<String> result = (Set)Cache.get(CACHE_KEY);
        if (result == null) {
            result = new HashSet<String>();
            TermEnum terms = Search.getCurrentStore().getIndexSearcher("models.JobApplication").getIndexReader().terms();
            if (terms.term() != null) {
                if ("tags".equals(terms.term().field())) {
                    result.add(terms.term().text());
                }
            }
            while (terms.next()) {
                if ("tags".equals(terms.term().field())) {
                    result.add(terms.term().text());
                }
            }
            Cache.set(CACHE_KEY, result, "30min");
        }
        return result;
    }

}
