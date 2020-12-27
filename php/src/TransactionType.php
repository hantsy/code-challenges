<?php

namespace TransactionAnalyser;

use MyCLabs\Enum\Enum;

// see:https://github.com/myclabs/php-enum
/**
 * @method static PAYMENT()
 * @method static REVERSAL()
 */
class TransactionType extends Enum
{
    private const PAYMENT = "PAYMENT";
    private const  REVERSAL = "REVERSAL";
}