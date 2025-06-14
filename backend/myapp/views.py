import re
import json
import cv2
import numpy as np
import pytesseract

from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate, login
from django.contrib.auth.models import User  # ✅ You missed this!
from rest_framework.decorators import api_view, parser_classes
from rest_framework.parsers import MultiPartParser, FormParser
from rest_framework.response import Response
from rest_framework import status


def extract_text_from_image_bytes(image_bytes):
    np_arr = np.frombuffer(image_bytes, np.uint8)
    img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    text = pytesseract.image_to_string(gray)
    return text


def parse_user_data(text):
    if not text:
        return {
            "name": "Unknown",
            "phone": "0000000000",
            "address": "N/A"
        }

    lines = text.split('\n')
    name = next((line for line in lines if "Name" in line), "Name: Unknown")
    phone = next((line for line in lines if re.search(r'\d{10}', line)), "Phone: 0000000000")
    address = next((line for line in lines if "Address" in line), "Address: N/A")

    return {
        "name": name.split(":")[-1].strip() if ":" in name else name.strip(),
        "phone": re.search(r'\d{10}', phone).group() if re.search(r'\d{10}', phone) else "0000000000",
        "address": address.split(":")[-1].strip() if ":" in address else address.strip()
    }


@api_view(['POST'])
@parser_classes([MultiPartParser, FormParser])
def get_image(request):
    if 'image' not in request.FILES:
        return Response({'error': 'No image file found in request.'}, status=400)

    image_file = request.FILES['image']
    image_bytes = image_file.read()

    try:
        text = extract_text_from_image_bytes(image_bytes)
        parsed_data = parse_user_data(text)
        return Response({
            'extracted_text': text,
            'parsed_data': parsed_data
        })
    except Exception as e:
        return Response({'error': str(e)}, status=500)


@api_view(['GET'])
def api_home(request):
    return Response({"message": "Hello, DRF!"})


@csrf_exempt
def parse_user_data_view(request):
    if request.method == 'GET':
        text = request.GET.get('text', '')
        if not text:
            return JsonResponse({'error': 'Missing "text" query parameter'}, status=400)
        parsed = parse_user_data(text)
        return JsonResponse(parsed)
    else:
        return JsonResponse({'error': 'Only GET method allowed'}, status=405)


def login_or_register(user_data, db_file='users.json'):
    try:
        with open(db_file, 'r') as f:
            users = json.load(f)
    except FileNotFoundError:
        users = {}

    if user_data['phone'] in users:
        print(f"✅ Logged in as {users[user_data['phone']]['name']}")
    else:
        users[user_data['phone']] = user_data
        with open(db_file, 'w') as f:
            json.dump(users, f, indent=4)
        print(f"🆕 Registered and logged in as {user_data['name']}")


@api_view(['POST'])
def user_login(request):
    username = request.data.get('username')
    password = request.data.get('password')

    if not username or not password:
        return Response(
            {'error': 'Please provide both username and password'},
            status=status.HTTP_400_BAD_REQUEST
        )

    user = authenticate(request, username=username, password=password)
    if user is not None:
        login(request, user)
        return Response(
            {'message': 'Everything is OK ✅'},
            status=status.HTTP_200_OK
        )
    else:
        return Response(
            {'error': 'Invalid credentials'},
            status=status.HTTP_401_UNAUTHORIZED
        )


@api_view(['POST'])
def register_user(request):
    username = request.data.get('username')
    email = request.data.get('email')
    password = request.data.get('password')

    if not username or not password:
        return Response(
            {'error': 'Username and password are required.'},
            status=status.HTTP_400_BAD_REQUEST
        )

    if User.objects.filter(username=username).exists():
        return Response(
            {'error': 'Username already exists.'},
            status=status.HTTP_400_BAD_REQUEST
        )

    user = User.objects.create_user(username=username, email=email, password=password)
    user.save()

    return Response(
        {'message': 'User registered successfully.'},
        status=status.HTTP_201_CREATED
    )