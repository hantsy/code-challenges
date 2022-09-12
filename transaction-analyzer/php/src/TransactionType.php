<?php

namespace TransactionAnalyser;

// PHP 8.1 introduces enum.
// see:https://github.com/myclabs/php-enum
enum TransactionType:string
{
    case PAYMENT="PAYMENT";
    case REVERSAL="REVERSAL";
}

