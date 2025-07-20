<h1>Calculator Invoice</h1>
<p>Date: {{ now()->format('Y-m-d H:i:s') }}</p>
<table border="1">
    <tr>
        <th>Expression</th>
        <th>Result</th>
        <th>Date & Time</th>
    </tr>
    @foreach ($history as $item)
    <tr>
        <td>{{ $item['expression'] }}</td>
        <td>{{ $item['result'] }}</td>
        <td>{{ $item['timestamp'] }}</td>
    </tr>
    @endforeach
</table>
<p style="margin-top: 20px;">Thank you for using our calculator!</p>