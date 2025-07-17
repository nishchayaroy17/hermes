from flask import Flask, request, jsonify

app = Flask(__name__)

# Store all received data in a list
received_data = []

@app.route('/location', methods=['POST'])
def receive_location():
    try:
        # Get JSON payload from request
        data = request.get_json(force=True)
        if not data:
            return jsonify({"status": "error", "message": "No JSON received"}), 400

        # Store and print the data
        received_data.append(data)
        print(f"Received data: {data}")

        return jsonify({"status": "success", "message": "Data received"}), 200

    except Exception as e:
        print(f"Error: {e}")
        return jsonify({"status": "error", "message": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=9590, debug=True)
