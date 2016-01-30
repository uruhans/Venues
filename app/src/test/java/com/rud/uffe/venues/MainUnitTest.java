package com.rud.uffe.venues;

import android.test.suitebuilder.annotation.SmallTest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * For now we only test the interface and handling of empty string response
 */
@SmallTest
public class MainUnitTest {
    @Test
    public void venue_check_interface() throws Exception {
        //check the presenters callback:doSearch()
        MainActivityMock mainActivity = new MainActivityMock();
        mainActivity.checkDoSearch("abc", "12,53");
        assertEquals(mainActivity.getBeenThere(), true);

        //check the presenters callback:showSearchResults()
        MainActivityMock mainActivity1 = new MainActivityMock();
        mainActivity1.showSearchResults();
        assertEquals(mainActivity.getBeenThere(), true);

        //check the presenters callback:showError()
        MainActivityMock mainActivity2 = new MainActivityMock();
        mainActivity.handleJson("");
        assertEquals(mainActivity.getBeenThere(), true);
    }

    @Test
    public void venue_parsing_empty_response() throws Exception {
        //check the presenters callback:showerror and validate RequestState
        MainActivityMock mainActivity = new MainActivityMock();
        mainActivity.handleJson("");
        MainPresenter.RequestState requestState = mainActivity.getRequestState();
        assertEquals(requestState, MainPresenter.RequestState.EMPTY_RESPONSE_STR);
    }
}