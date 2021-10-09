package ltd.fdsa.switcher.core.cbor;

import ltd.fdsa.switcher.core.cbor.model.DataItem;

/**
 * Callback interface for a streaming {@link CborDecoder}.
 */
public interface DataItemListener {

    /**
     * Gets called on every decoded {@link DataItem}.
     * 
     * @param dataItem the {@link DataItem}
     */
    void onDataItem(DataItem dataItem);

}
