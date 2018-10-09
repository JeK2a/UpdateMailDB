package com;

import com.chilkatsoft.*;

public class ChilkatExample {

    static {
        try {
            System.loadLibrary("chilkat");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    public static void main(String argv[])
    {
        CkImap imap = new CkImap();

        boolean success;

        //  Anything unlocks the component and begins a fully-functional 30-day trial.
        success = imap.UnlockComponent("Anything for 30-day trial");
        if (success != true) {
            System.out.println(imap.lastErrorText());
            return;
        }

        //  Connect to an IMAP server.
        //  Use TLS
        imap.put_Ssl(true);
        imap.put_Port(993);
        success = imap.Connect("imap.yandex.com");
        if (success != true) {
            System.out.println(imap.lastErrorText());
            return;
        }

        //  Login
        success = imap.Login("jek2ka2016@yandex.ru","Nokia3510!");
        if (success != true) {
            System.out.println(imap.lastErrorText());
            return;
        }

        //  Select an IMAP mailbox
        success = imap.SelectMailbox("Inbox");
        if (success != true) {
            System.out.println(imap.lastErrorText());
            return;
        }

        CkMessageSet messageSet;
        //  We can choose to fetch UIDs or sequence numbers.
        boolean fetchUids = true;

        //  Here are examples of different search criteria:

        //  Return all messages.
        String allMsgs = "ALL";

        //  Search for already-answered emails.
        String answered = "ANSWERED";

        //  Search for messages on a specific date.
        //  The date string is DD-Month-YYYY where Month is
        //  Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, or Dec.
        String onDate = "SENTON 05-Mar-2007";

        //  Search for messages between two dates.  SENTBEFORE
        //  finds emails sent before a date, and SENTSINCE finds
        //  email sent on or after a date.  The "AND" operation
        //  is implied by joining criteria, separated by spaces.
        String betweenDates = "SENTSINCE 01-Mar-2007 SENTBEFORE 05-Mar-2007";

        //  Another example of AND: find all unanswered emails
        //  sent after 04-Mar-2007 with "Problem" in the subject:
        String complexSearch1 = "UNANSWERED SENTSINCE 04-Mar-2007 Subject \"Problem\"";

        //  Find messages with a specific string in the body:
        String bodySearch = "BODY \"problem solved\"";

        //  Using OR.  The syntax is OR <criteria1> <criteria2>.
        //  The "OR" comes first, followed by each criteria.
        //  For example, to match all emails with "Help" or "Question" in the subject.
        //  You'll notice that literal strings may be quoted or unquoted.
        //  If a literal contains SPACE characters, quote it:
        String orSearch = "OR SUBJECT Help SUBJECT Question";

        //  ----------------------------------------------
        //  Strings are case-insensitive when searching....
        //  ----------------------------------------------

        //  Find all emails sent from yahoo.com addresses:
        String fromSearch = "FROM yahoo.com";
        //  Find all emails sent from anyone with "John" in their name:
        String johnSearch = "FROM John";

        //  Find emails with the RECENT flag set:
        String recentSearch = "RECENT";

        //  Find emails that don't have the recent flag set:
        String notRecentSearch = "NOT RECENT";
        //  This is synonymous with "OLD":
        String oldSearch = "OLD";

        //  Find all emails marked for deletion:
        String markedForDeleteSearch = "DELETED";

        //  Find all emails having a specified header field with a value
        //  containing a substring:
        String headerSearch = "HEADER DomainKey-Signature paypal.com";

        //  Find any emails having a specific header field.  If the
        //  2nd argument to the "HEADER" criteria is an empty string,
        //  any email having the header field is returned regardless
        //  of the header field's content.
        //  Find any emails with a DomainKey-Signature field:
        String headerExistsSearch = "HEADER DomainKey-Signature \"\"";

        //  Find NEW emails: these are emails that have the RECENT flag
        //  set, but not the SEEN flag:
        String newSearch = "NEW";

        //  Find emails larger than a certain number of bytes:
        String sizeLargerSearch = "LARGER 500000";

        //  Find emails marked as seen or not already seen:
        String seenSearch = "SEEN";
        String notSeenSearch = "NOT SEEN";

        //  Find emails having a given substring in the TO header field:
        String toSearch = "TO support@chilkatsoft.com";
        //  A more long-winded way to do the same thing:
        String toSearch2 = "HEADER TO support@chilkatsoft.com";

        //  Find emails smaller than a size in bytes:
        String smallerSearch = "SMALLER 30000";

        //  Find emails that have a substring anywhere in the header
        //  or body:
        String fullSubstringSearch = "TEXT \"Zip Component\"";

        //  Pass any of the above strings here to test a search:
        messageSet = imap.Search(orSearch, fetchUids);
        if (messageSet == null ) {
            System.out.println(imap.lastErrorText());
            return;
        }

        //  Fetch the email headers into a bundle object:
        CkEmailBundle bundle;
        bundle = imap.FetchHeaders(messageSet);
        if (bundle == null ) {
            System.out.println(imap.lastErrorText());
            return;
        }

        //  Display the Subject and From of each email.
        for (int i = 0; i <= bundle.get_MessageCount() - 1; i++) {
            CkEmail email;
            email = bundle.GetEmail(i);

            System.out.println(email.getHeaderField("Date"));
            System.out.println(email.subject());
            System.out.println(email.ck_from());
            System.out.println("--");

        }

        //  Disconnect from the IMAP server.
        success = imap.Disconnect();


    }
}