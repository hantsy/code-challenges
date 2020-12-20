<?php

namespace Hantsy\TransactionAnalyser;

use MyCLabs\Enum\Enum;

class TransactionType extends Enum
{
    const PAYMENT = "PAYMENT";
    const REVERSAL = "REVERSAL";
}