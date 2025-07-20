<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Session;
use PDF;

class CalculatorController extends Controller
{
    public function index()
    {
        return view('casio');
    }

    public function calculate(Request $request)
    {
        $expression = $request->input('expression');
        try {
            $result = eval("return $expression;");
            $history = Session::get('history', []);
            $history[] = [
                'expression' => $expression,
                'result' => $result,
                'timestamp' => now()->format('Y-m-d H:i:s')
            ];
            Session::put('history', $history);
        } catch (\Throwable $e) {
            $result = 'Error';
        }

        return response()->json(['result' => $result]);
    }

    public function history()
    {
        return response()->json(Session::get('history', []));
    }

    public function clearHistory()
    {
        Session::forget('history');
        return back();
    }

    public function exportPdf()
    {
        $history = Session::get('history', []);
        $pdf = PDF::loadView('exports.pdf', compact('history'));
        return $pdf->download('calculator_history.pdf');
    }
}