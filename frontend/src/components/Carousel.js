import React, { useState, useEffect } from 'react';

const Carousel = () => {
  
  const slides = [
    {
      id: 1,
      image: '/img/img4.png',
      title: 'PUMA Flat 30% Offer',
      subtitle: 'Upgrade your wardrobe with the latest collection',
    },
    {
      id: 2,
      image: '/img/img2.png',
      title: 'Exclusive Footwear Collection',
      subtitle: 'Get the best deals on top brands',
    },
    {
      id: 3,
      image: '/img/img4.png',
      title: 'Luxury Watches Collection',
      subtitle: 'Stylish and premium designs for all occasions',
    },
  ];

  const [currentIndex, setCurrentIndex] = useState(0);

  // Auto-slide functionality
  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentIndex((prevIndex) => (prevIndex === slides.length - 1 ? 0 : prevIndex + 1));
    }, 5000);
    
    return () => clearInterval(interval);
  }, [slides.length]);

  const goToSlide = (index) => {
    setCurrentIndex(index);
  };

  const goToPrevious = () => {
    setCurrentIndex((prevIndex) => (prevIndex === 0 ? slides.length - 1 : prevIndex - 1));
  };

  const goToNext = () => {
    setCurrentIndex((prevIndex) => (prevIndex === slides.length - 1 ? 0 : prevIndex + 1));
  };

  return (
    <div className="relative h-[500px] mt-16">
      {/* Main Slide */}
      <div className="w-full h-full overflow-hidden relative">
        {slides.map((slide, index) => (
          <div 
            key={slide.id}
            className={`absolute top-0 left-0 w-full h-full transition-opacity duration-700 ease-in-out ${
              index === currentIndex ? 'opacity-100 z-10' : 'opacity-0 z-0'
            }`}
          >
            <img
              src={slide.image}
              alt={slide.title}
              className="w-full h-full object-cover"
            />
            <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center">
              <div className="text-center text-white px-4 py-6 rounded-lg bg-black bg-opacity-60">
                <h2 className="text-4xl font-bold mb-2">{slide.title}</h2>
                <p className="text-xl mb-4">{slide.subtitle}</p>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Navigation Buttons */}
      <button 
        onClick={goToPrevious} 
        className="absolute left-4 top-1/2 transform -translate-y-1/2 z-20 bg-black bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-70"
      >
        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
        </svg>
      </button>
      <button 
        onClick={goToNext} 
        className="absolute right-4 top-1/2 transform -translate-y-1/2 z-20 bg-black bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-70"
      >
        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
        </svg>
      </button>

      {/* Indicators */}
      <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 z-20 flex space-x-2">
        {slides.map((_, index) => (
          <button
            key={index}
            onClick={() => goToSlide(index)}
            className={`w-3 h-3 rounded-full ${
              index === currentIndex ? 'bg-primary' : 'bg-white bg-opacity-50'
            }`}
          />
        ))}
      </div>
    </div>
  );
};

export default Carousel; 